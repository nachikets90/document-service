package com.qdb.controller;

import java.io.IOException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.qdb.dto.FileDTO;
import com.qdb.service.FileUtilityService;

@RestController
@RequestMapping("/api/v1/files/")
public class FileUtilityController {

	@Autowired
	private FileUtilityService service;

	@PostMapping("/upload")
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {

		String fileTobeUploaded = service.uploadNewFile(file);
		return ResponseEntity.status(HttpStatus.OK).body(fileTobeUploaded);
	}

	@GetMapping("/downlaod/{fileName}")
	public ResponseEntity<?> downloadFile(@PathVariable String fileName) throws IOException {

		byte[] downLoadFile = service.downloadFile(fileName);

		if (Objects.isNull(downLoadFile)) {
			return new ResponseEntity("There is no such file", HttpStatus.NOT_FOUND);

		}

		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("application/pdf"))
				.body(downLoadFile);
	}

	@GetMapping("/all")
	public ResponseEntity<?> retriveAllFile() throws IOException {

		return new ResponseEntity(service.retriveAllFiles(), HttpStatus.FOUND);
	}

	@GetMapping("/get/{fileName}")
	public ResponseEntity<?> getDocument(@PathVariable String fileName) throws IOException {

		FileDTO dto = service.getDocument(fileName);

		if (Objects.isNull(dto)) {
			return new ResponseEntity("There is no such file", HttpStatus.NOT_FOUND);

		}

		return new ResponseEntity(dto, HttpStatus.FOUND);
	}
	@GetMapping("/getByID/{id}")
	public ResponseEntity<?> getByID(@PathVariable int id) throws IOException {

		FileDTO dto = service.getDocumentByID(id);

		if (Objects.isNull(dto)) {
			return new ResponseEntity("There is no such file", HttpStatus.NOT_FOUND);

		}

		return new ResponseEntity(dto, HttpStatus.FOUND);
	}

	@DeleteMapping("/delete/{name}")
	public ResponseEntity<?> deleteDocument(@PathVariable String name) throws IOException {

		String msg = service.deleteDocument(name);

		return ResponseEntity.status(HttpStatus.OK).body(msg);
	}

	@PutMapping("/update")
	public ResponseEntity<?> updtaeDocument(@RequestParam("file") MultipartFile file) throws IOException {

		String msg = service.updateFile(file);

		return ResponseEntity.status(HttpStatus.OK).body(msg);
	}
}
