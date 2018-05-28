package kafka.clients;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.acl.AccessControlEntry;
import org.apache.kafka.common.acl.AccessControlEntryFilter;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.acl.AclBindingFilter;
import org.apache.kafka.common.acl.AclOperation;
import org.apache.kafka.common.acl.AclPermissionType;
import org.apache.kafka.common.resource.Resource;
import org.apache.kafka.common.resource.ResourceFilter;
import org.apache.kafka.common.resource.ResourceType;

public class AdminAcl {

	public static CreateTopicsResult createTopicWithPrefix(AdminClient adminAcl, String name, int partitions,
			int replication) {
		NewTopic t = new NewTopic(name, partitions, (short) replication);
		CreateTopicsResult result = adminAcl.createTopics(Arrays.asList(t));
		return result;
	}

	public static void main(String[] args) {

		Properties config = new Properties();

		config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
		config.put("security.protocol", "SASL_PLAINTEXT");
		config.put("sasl.mechanism", "PLAIN");

		config.put("sasl.jaas.config",
				"org.apache.kafka.common.security.plain.PlainLoginModule required username=\"admin\" password=\"admin-secret\";");

		AdminClient adminAcl = AdminClient.create(config);

		CreateTopicsResult r = createTopicWithPrefix(adminAcl, "ontology_product", 1, 1);
		CreateTopicsResult rtest = createTopicWithPrefix(adminAcl, "ontology_HelsinkiPopulation", 1, 1);

		Collection<AclBinding> acls = new ArrayList<>();
		Resource resource = new Resource(ResourceType.TOPIC, "test");
		AccessControlEntry aclEntry = new AccessControlEntry("User:admin", "*", AclOperation.READ,
				AclPermissionType.ALLOW);
		AclBinding aclBinding = new AclBinding(resource, aclEntry);
		acls.add(aclBinding);

		adminAcl.createAcls(acls);

		Collection<AclBindingFilter> deleteAcls = new ArrayList<>();
		AclBindingFilter aclDel = new AclBindingFilter(new ResourceFilter(ResourceType.TOPIC, "test"),
				new AccessControlEntryFilter("User:admin", "*", AclOperation.READ, AclPermissionType.DENY));
		deleteAcls.add(aclDel);
		adminAcl.deleteAcls(deleteAcls);

		adminAcl.close();
	}
}
