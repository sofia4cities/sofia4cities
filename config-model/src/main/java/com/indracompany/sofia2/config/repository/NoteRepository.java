package com.indracompany.sofia2.config.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.indracompany.sofia2.config.model.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {

}
