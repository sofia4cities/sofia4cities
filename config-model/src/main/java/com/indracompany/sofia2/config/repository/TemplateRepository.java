package com.indracompany.sofia2.config.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.indracompany.sofia2.config.model.Template;
import java.util.List;




public interface TemplateRepository extends JpaRepository<Template, String> {
	
	List<Template> findByTypeAndIdentificationAndDescription(String type, String identification, String description);
	List<Template> findByIdentification(String identification);
	List<Template> findByIsrelationalTrue();
	List<Template> findByIsrelationalFalse();
	List<Template> findByType(String type);
	List<Template> findByCategory(String category);
	long countByIdentification(String identification);
	long countByType(String type);
}
