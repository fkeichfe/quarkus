package io.quarkus.hibernate.orm.runtime;

import java.util.Locale;

import javax.enterprise.inject.Default;

import io.quarkus.arc.Arc;
import io.quarkus.arc.InjectableInstance;
import io.quarkus.hibernate.orm.PersistenceUnit;

public class PersistenceUnitUtil {

    public static final String DEFAULT_PERSISTENCE_UNIT_NAME = "<default>";

    public static boolean isDefaultPersistenceUnit(String name) {
        return DEFAULT_PERSISTENCE_UNIT_NAME.equals(name);
    }

    public static <T> InjectableInstance<T> singleInstanceForPersistenceUnit(Class<T> beanType, String persistenceUnitName) {
        InjectableInstance<T> instance = instanceForPersistenceUnit(beanType, persistenceUnitName);
        if (instance.isAmbiguous()) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "Multiple instances of %1$s were found for persistence unit %2$s. "
                            + "At most one instance can be assigned to each persistence unit.",
                    beanType.getSimpleName(), persistenceUnitName));
        }
        return instance;
    }

    public static <T> InjectableInstance<T> instanceForPersistenceUnit(Class<T> beanType, String persistenceUnitName) {
        InjectableInstance<T> instance;
        if (isDefaultPersistenceUnit(persistenceUnitName)) {
            instance = Arc.container().select(beanType, Default.Literal.INSTANCE);
        } else {
            instance = Arc.container().select(beanType,
                    new PersistenceUnit.PersistenceUnitLiteral(persistenceUnitName));
        }
        return instance;
    }
}
