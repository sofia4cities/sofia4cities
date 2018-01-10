/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.config.repository;

import java.util.List;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.config.model.Note;
import com.indracompany.sofia2.config.repository.NoteRepository;

/**
 *
 * @author Luis Miguel Gracia
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NoteRepositoryIntegrationTest {

	@Autowired
	NoteRepository repository;

	@Test
	public void test1_Insert() {
		Note note = new Note();
		note.setContent("Contenido de Nota 1");
		note.setTitle("Nota 1");
		this.repository.save(note);
		Assert.assertTrue(this.repository.count()==1L);

	}

	@Test
	public void test2_GetAll() {
		List<Note> notes = this.repository.findAll();
		Assert.assertTrue(notes.size()==1);
	}


	@Test
	public void test3_Delete() {
		List<Note> notes = this.repository.findAll();
		Assert.assertTrue(notes.size()==1);
		repository.delete(notes.get(0));
		notes = this.repository.findAll();
		Assert.assertTrue(notes==null || notes.isEmpty());
	}



}
