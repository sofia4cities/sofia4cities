package com.indracompany.sofia2.scheduler.scheduler.instance;

import static com.indracompany.sofia2.scheduler.PropertyNames.SCHEDULER_PROPERTIES_LOCATION;

import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import com.indracompany.sofia2.scheduler.scheduler.BatchScheduler;
import com.indracompany.sofia2.scheduler.scheduler.GenericBatchScheduler;
import com.indracompany.sofia2.scheduler.scheduler.GenericQuartzConfig;

@Configuration
@ConditionalOnResource(resources = SCHEDULER_PROPERTIES_LOCATION)
public class BatchQuartzConfig extends GenericQuartzConfig {

	private final String SCHEDULER_BEAN_FACTORY_NAME = "batch-scheduler-factory";

	@Bean(SCHEDULER_BEAN_FACTORY_NAME)
	public SchedulerFactoryBean batchSchedulerFactoryBean(JobFactory jobFactory,
			PlatformTransactionManager transactionManager) throws SchedulerException {
		return getSchedulerFactoryBean(jobFactory, transactionManager);
	}

	@Bean(SchedulerNames.BATCH_SCHEDULER_NAME)
	public BatchScheduler batchScheduler(
			@Autowired @Qualifier(SCHEDULER_BEAN_FACTORY_NAME) SchedulerFactoryBean schedulerFactoryBean) {
		return new GenericBatchScheduler(schedulerFactoryBean.getScheduler(), getSchedulerBeanName());
	}

	@Override
	public String getSchedulerBeanName() {
		return SchedulerNames.BATCH_SCHEDULER_NAME;
	}

}
