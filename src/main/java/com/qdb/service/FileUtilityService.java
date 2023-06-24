package com.qdb.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.qdb.dto.FileDTO;
import com.qdb.entity.DocumentName;
import com.qdb.repository.FileContentRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileUtilityService {

	@Autowired
	private FileContentRepository fileRepository;

	@Value("${file.server.path}")
	private String FOLDER_PATH;

	public String uploadNewFile(MultipartFile file) throws IOException {
		String fileName = file.getOriginalFilename();
		DocumentName doc=checkFileExists(fileName);
		if (Objects.isNull(doc)) {
			String filePath = FOLDER_PATH + fileName;
			uploadFileToSystem(file, filePath);
			return "file uploaded successfully : " + filePath;
		} else {
			return "There is already a file with this name : " + fileName;
		}

	}

	public String updateFile(MultipartFile file) throws IOException {
		String fileName = file.getOriginalFilename();
		DocumentName doc=checkFileExists(fileName);
		if (Objects.isNull(doc)) {
			return "Please upload this file as there is no file with this name: " + fileName;
		}else {
			deleteDocument(fileName);
			String filePath = FOLDER_PATH + fileName;
			uploadFileToSystem(file, filePath);
			return "File with name-> " + fileName + " updated";
		}

	}

	public byte[] downloadFile(String fileName) throws IOException {

		DocumentName doc=checkFileExists(fileName);
		if(!Objects.isNull(doc)) {
			String filePath = doc.getFilePath();
			log.info("filePath->" + filePath);
			
			byte[] images = Files.readAllBytes(new File(filePath).toPath());
			return images;
		}
		else
			return null;
		
	}
	public FileDTO getDocumentByID(int  id) throws IOException {
		Optional<DocumentName> doc =fileRepository.findById(id);
		if(doc.isPresent()) {
			DocumentName document= doc.get();
			FileDTO dto=returnDto(document);
			return dto;
		}
		else
			return null;
		

	}
	public FileDTO getDocument(String fileName) throws IOException {

		DocumentName doc=checkFileExists(fileName);
		if(!Objects.isNull(doc)) {
			return returnDto(doc);
		}else
			return null;

	}

	public String deleteDocument(String name) throws IOException {
		DocumentName doc=checkFileExists(name);
		String msg = null;
		if (Objects.isNull(doc)) {
			log.info("No file Found");
			msg = "No Such File found";
			return msg;
		}
		int id = doc.getId();
		fileRepository.deleteById(id);
		msg = "Documnt deleted with name-> " + name;
		return msg;
	}

	public List<FileDTO> retriveAllFiles() throws IOException {

		List<DocumentName> list = fileRepository.findAll();
		List<FileDTO> list1 = list.stream().map(fc -> returnDto(fc)).collect(Collectors.toList());
		return list1;

	}

	private FileDTO returnDto(DocumentName content) {
		String url1 = "http://localhost:8083/api/v1/files/downlaod/";
		String fileName = content.getName();
		return FileDTO.builder().id(content.getId()).name(fileName).url(url1 + fileName).build();
	}

	private void uploadFileToSystem(MultipartFile file, String filePath) {

		DocumentName fileContent = DocumentName.builder().name(file.getOriginalFilename()).type(file.getContentType())
				.filePath(filePath).build();
		// saving file details to db
		fileRepository.save(fileContent);

		byte[] bytes;
		try {
			bytes = file.getBytes();
			Path path = Paths.get(filePath);
			Files.write(path, bytes);
		} catch (IOException e) {

			e.printStackTrace();
		}

	}
	
	private DocumentName checkFileExists(String fileName) {
		Optional<DocumentName> filOptional = fileRepository.findByName(fileName);
		DocumentName doc=filOptional.isPresent()?filOptional.get():null;
		
		return doc;
		
	}

}
