/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
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
package com.indracompany.sofia2.rtdbmaintainer.job;

import static org.mockito.Mockito.when;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.commons.testing.IntegrationTest;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.Ontology.RtdbCleanLapse;
import com.indracompany.sofia2.config.model.Ontology.RtdbDatasource;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.persistence.mongodb.template.MongoDbTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Category(IntegrationTest.class)
@Ignore
public class RtdbMaintainerJobTest {

	@Autowired
	RtdbMaintainerJob job;
	@Autowired
	private MongoDbTemplate mongoDbConnector;
	@Mock
	JobExecutionContext jobContext;
	@Mock
	JobDetail jobDetail;
	@Mock
	JobDataMap jobData;
	@Spy
	Ontology ontology;

	@MockBean
	private OntologyService ontologyService;
	@Value("${sofia2.database.mongodb.export.path:#{null}}")
	private String mongoExport;
	private static final String MONGO_DATABASE = "sofia2_s4c";
	private static final String ONTOLOGY = "HelsinkiPopulation";

	@Before
	public void insertOntologies() {
		String data = "{\"year\":1993,\"population\":7000,\"population_women\":4000,\"population_men\":3000,\"contextData\":{\"timestampMillis\":100}}";
		mongoDbConnector.insert(MONGO_DATABASE, ONTOLOGY, data);
		this.init_mocks();

	}

	public void init_mocks() {
		MockitoAnnotations.initMocks(this);
		ontology.setRtdbClean(true);
		ontology.setRtdbCleanLapse(RtdbCleanLapse.OneYear);
		ontology.setIdentification(ONTOLOGY);
		ontology.setRtdbDatasource(RtdbDatasource.Mongo);
		List<Ontology> ontologies = new ArrayList<Ontology>();
		ontologies.add(ontology);
		when(this.jobContext.getJobDetail()).thenReturn(this.jobDetail);
		when(this.jobDetail.getJobDataMap()).thenReturn(jobData);
		when(this.jobData.getLong("timeout")).thenReturn((long) 40000);
		when(this.jobData.get("timeUnit")).thenReturn(TimeUnit.MINUTES);
		when(this.ontologyService.getCleanableOntologies()).thenReturn(ontologies);
	}

	@Test
	public void test_FileIsWritten_AndDataDeleted_Mongo() throws InterruptedException {
		this.job.execute(jobContext);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-dd-MM-hh-mm");
		String exportPath = mongoExport + ontology.getIdentification() + format.format(new Date()) + ".json";
		File fileExport = new File(exportPath);
		Thread.sleep(5000);
		Assert.assertTrue(fileExport.exists());
		Assert.assertTrue(fileExport.length() > 0);
		String query = "{\"contextData.timestampMillis\":{\"$eq\":100}}";
		Assert.assertTrue(mongoDbConnector.remove(MONGO_DATABASE, ONTOLOGY, query) == 0);

	}

}
