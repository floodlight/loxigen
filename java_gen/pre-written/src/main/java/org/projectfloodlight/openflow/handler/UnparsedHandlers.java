package org.projectfloodlight.openflow.handler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.projectfloodlight.openflow.exceptions.OFParseError;
import org.slf4j.Logger;

public class UnparsedHandlers {
    private volatile static UnparsedHandler defaultHandler = throwOnUnparsed();

    public static UnparsedHandler throwOnUnparsed() {
        return ThrowOnUnparsedHandler.INSTANCE;
    }

    public static UnparsedHandler log(Logger logger) {
        return new LogUnparsedHandler(logger);
    }

    public static void setDefaultHandler(UnparsedHandler handler) {
        UnparsedHandlers.defaultHandler = handler;
    }

    public static UnparsedHandler getDefaultHandler() {
        return defaultHandler;
    }


    /** UnparsedHandler that keeps count of the unknown messages by class and discrimantor.
     *
     *  Also emits a warn-level for the first such message encountered of each class and
     *  discriminator value.
     *
     * @author Andreas Wundsam <andreas.wundsam@bigswitch.com>
     */
    static class LogUnparsedHandler implements UnparsedHandler {
        private final ConcurrentMap<String, ConcurrentMap<Object, AtomicInteger>> classnameDescriminatorCount =
                new ConcurrentHashMap<>();

        private final Logger logger;

        public LogUnparsedHandler(Logger logger) {
            this.logger = logger;
        }

        @Override
        public void unparsedMessage(Class<?> parentClass, String discriminatorName,
                Object value) throws OFParseError {
            AtomicInteger count = getCountInteger(parentClass, value);

            if(count.getAndIncrement() == 0) {
                logger.warn("Unknown value for discriminator {} of class {}: {}. Message ignored.",
                    discriminatorName, parentClass.getSimpleName(), value);
            } else {
                logger.debug("Unknown value for discriminator {} of class {}: {}. Message ignored.",
                        discriminatorName, parentClass.getSimpleName(), value);
            }
        }

        private AtomicInteger getCountInteger(Class<?> msgClass, Object discriminator) {
            ConcurrentMap<Object, AtomicInteger> mapForClass = getMapForClass(msgClass);
            final AtomicInteger count = mapForClass.get(discriminator);
            if(count == null) {
                final AtomicInteger newCount = new AtomicInteger(0);
                final AtomicInteger presentCount = mapForClass.put(discriminator, newCount);
                return presentCount != null ? presentCount : newCount;
            } else {
                return count;
            }
        }

        private ConcurrentMap<Object, AtomicInteger> getMapForClass(Class<?> msgClass) {
            String name = msgClass.getName();

            final ConcurrentMap<Object, AtomicInteger> map = classnameDescriminatorCount.get(name);
            if(map == null) {
                final ConcurrentMap<Object, AtomicInteger> newMap = new ConcurrentHashMap<>();
                final ConcurrentMap<Object, AtomicInteger> presentMap =
                        classnameDescriminatorCount.putIfAbsent(name, newMap);
                return presentMap != null ? presentMap : newMap;
            } else {
                return map;
            }
        }
    }


    /** UnparsedHandler that throws an exception.
     */
    static class ThrowOnUnparsedHandler implements UnparsedHandler {
        private final static ThrowOnUnparsedHandler INSTANCE = new ThrowOnUnparsedHandler();

        @Override
        public void unparsedMessage(Class<?> parentClass, String discriminatorName,
                Object value) throws OFParseError {
            String msg = String.format("Unknown value for discriminator %s of class %s: %s",
                    discriminatorName, parentClass.getSimpleName(), value);

            throw new OFParseError(msg);
        }
    }


    public static void handleUnparsed(Class<?> msgClass, String discriminatorName, byte value) throws OFParseError {
        getDefaultHandler().unparsedMessage(msgClass, discriminatorName, Byte.valueOf(value));
    }

    public static void handleUnparsed(Class<?> msgClass, String discriminatorName, int value) throws OFParseError {
        getDefaultHandler().unparsedMessage(msgClass, discriminatorName, Integer.valueOf(value));
    }
}
