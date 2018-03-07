/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baeldung.springbootadminserver;

import de.codecentric.boot.admin.notify.Notifier;
import de.codecentric.boot.admin.notify.RemindingNotifier;
import de.codecentric.boot.admin.notify.filter.FilteringNotifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.monitoring.configs.NotifierConfiguration;

import static org.junit.Assert.assertNotEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { NotifierConfiguration.class }, webEnvironment = NONE)
public class NotifierConfigurationIntegrationTest {

    @Autowired private ApplicationContext applicationContext;

    @Test
    public void given_TheApplicatonContext_When_ItStarts_Then_NotifierBeanIsCreated() {
        Notifier notifier = (Notifier) applicationContext.getBean("notifier");
        assertNotEquals(notifier, null);
    }

    @Test
    public void given_TheApplicatonContext_When_ItStarts_Then_FilteringNotifierBeanIsCreated() {
        FilteringNotifier filteringNotifier = (FilteringNotifier) applicationContext.getBean("filteringNotifier");
        assertNotEquals(filteringNotifier, null);
    }

    @Test
    public void given_TheApplicatonContext_When_ItStarts_Then_RemindingNotifierBeanIsCreated() {
        RemindingNotifier remindingNotifier = (RemindingNotifier) applicationContext.getBean("remindingNotifier");
        assertNotEquals(remindingNotifier, null);
    }

}
