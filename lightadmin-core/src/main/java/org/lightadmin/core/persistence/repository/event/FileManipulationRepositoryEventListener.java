/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lightadmin.core.persistence.repository.event;

import org.lightadmin.core.config.domain.DomainTypeAdministrationConfiguration;
import org.lightadmin.core.config.domain.GlobalAdministrationConfiguration;
import org.lightadmin.core.persistence.metamodel.PersistentPropertyType;
import org.lightadmin.core.storage.FileResourceStorage;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.SimplePropertyHandler;

public class FileManipulationRepositoryEventListener extends ManagedRepositoryEventListener {

    private final FileResourceStorage fileResourceStorage;

    public FileManipulationRepositoryEventListener(GlobalAdministrationConfiguration configuration, FileResourceStorage fileResourceStorage) {
        super(configuration);
        this.fileResourceStorage = fileResourceStorage;
    }

    @Override
    protected void onBeforeDelete(final Object entity) {
        Class<?> domainType = entity.getClass();
        DomainTypeAdministrationConfiguration domainTypeAdministrationConfiguration = this.configuration.forManagedDomainType(domainType);

        PersistentEntity<?, ?> persistentEntity = domainTypeAdministrationConfiguration.getPersistentEntity();

        persistentEntity.doWithProperties(new PersistentPropertyFileDeletionHandler(entity));
    }

    private class PersistentPropertyFileDeletionHandler implements SimplePropertyHandler {
        private final Object entity;

        public PersistentPropertyFileDeletionHandler(Object entity) {
            this.entity = entity;
        }

        @Override
        public void doWithPersistentProperty(PersistentProperty<?> property) {
            if (PersistentPropertyType.isOfFileReferenceType(property)) {
                fileResourceStorage.delete(entity, property);
            }
        }
    }
}