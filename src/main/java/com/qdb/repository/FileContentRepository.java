package com.qdb.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qdb.entity.DocumentName;

@Repository
public interface FileContentRepository extends JpaRepository<DocumentName, Integer>{

	Optional<DocumentName> findByName(String fileName);
	
	@Modifying
	@Query("delete from DocumentName b where b.name=:name")
	void deleteByName(@Param("name") String name);
}
