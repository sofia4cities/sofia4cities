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
package com.indracompany.sofia2.config.services.webproject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.indracompany.sofia2.config.model.Role;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.model.WebProject;
import com.indracompany.sofia2.config.repository.WebProjectRepository;
import com.indracompany.sofia2.config.services.exceptions.WebProjectServiceException;
import com.indracompany.sofia2.config.services.user.UserService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WebProjectServiceImpl implements WebProjectService {

	@Autowired
	private WebProjectRepository webProjectRepository;

	@Autowired
	private UserService userService;

	@Value("${sofia2.webproject.baseurl:https://localhost:18080/web/}")
	private String rootWWW = "";

	@Value("${sofia2.webproject.rootfolder.path:/usr/local/webprojects/}")
	private String rootFolder;

	@Override
	public List<WebProject> getWebProjectsWithDescriptionAndIdentification(String userId, String identification,
			String description) {
		List<WebProject> webProjects;
		User user = this.userService.getUser(userId);

		description = description == null ? "" : description;
		identification = identification == null ? "" : identification;

		if (user.getRole().getId().equals(Role.Type.ROLE_ADMINISTRATOR.toString())) {
			webProjects = this.webProjectRepository
					.findByIdentificationContainingAndDescriptionContaining(identification, description);
		} else {
			webProjects = webProjectRepository.findByUserAndIdentificationContainingAndDescriptionContaining(user,
					identification, description);
		}

		return webProjects;
	}

	@Override
	public List<String> getWebProjectsIdentifications(String userId) {
		List<WebProject> webProjects;
		List<String> identifications = new ArrayList<String>();
		User user = this.userService.getUser(userId);

		if (user.getRole().getId().equals(Role.Type.ROLE_ADMINISTRATOR.toString())) {
			webProjects = this.webProjectRepository.findAllByOrderByIdentificationAsc();
		} else {
			webProjects = this.webProjectRepository.findByUserOrderByIdentificationAsc(user);
		}

		for (WebProject webProject : webProjects) {
			identifications.add(webProject.getIdentification());
		}

		return identifications;
	}

	@Override
	public List<String> getAllIdentifications() {
		List<WebProject> webProjects = this.webProjectRepository.findAll();
		List<String> allIdentifications = new ArrayList<String>();

		for (WebProject webProject : webProjects) {
			allIdentifications.add(webProject.getIdentification());
		}

		return allIdentifications;
	}

	public boolean webProjectExists(String identification) {
		if (this.webProjectRepository.findByIdentification(identification) != null)
			return true;
		else
			return false;
	}

	@Override
	public void createWebProject(WebProject webProject, String userId) {
		checkFolder(webProject.getIdentification());
		if (!webProjectExists(webProject.getIdentification())) {
			log.debug("Web Project does not exist, creating..");
			User user = this.userService.getUser(userId);
			webProject.setUser(user);
			if (webProject.getMainFile().isEmpty()) {
				webProject.setMainFile("index.html");
			}
			createFolderWebProject(webProject.getIdentification(), userId);
			this.webProjectRepository.save(webProject);

		} else {
			throw new WebProjectServiceException(
					"Web Project with identification:" + webProject.getIdentification() + " already exists");
		}
	}

	@Override
	public WebProject getWebProjectById(String webProjectId, String userId) {
		WebProject webProject = webProjectRepository.findById(webProjectId);
		User user = this.userService.getUser(userId);
		if (webProject != null) {
			if (hasUserPermissionToEditWebProject(user, webProject)) {
				return webProject;
			} else {
				throw new WebProjectServiceException("The user is not authorized");
			}
		} else {
			return null;
		}

	}

	public boolean hasUserPermissionToEditWebProject(User user, WebProject webProject) {
		if (user.getRole().getId().equals(Role.Type.ROLE_ADMINISTRATOR.toString())) {
			return true;
		} else if (webProject.getUser().getUserId().equals(user.getUserId())) {
			return true;
		} else {
			return false;
		}
	}

	public String getWebProjectURL(String identification) {
		WebProject webProject = webProjectRepository.findByIdentification(identification);
		return rootWWW + webProject.getIdentification() + "/" + webProject.getMainFile();
	}

	@Override
	public void updateWebProject(WebProject webProject, String userId) {
		WebProject wp = this.webProjectRepository.findById(webProject.getId());
		User user = this.userService.getUser(userId);

		if (wp != null) {
			if (hasUserPermissionToEditWebProject(user, wp)) {
				if (!webProjectExists(webProject.getIdentification())) {
					wp.setDescription(webProject.getDescription());
					wp.setMainFile(webProject.getMainFile());
					updateFolderWebProject(webProject.getIdentification(), userId);
					this.webProjectRepository.save(wp);
				} else {
					throw new WebProjectServiceException(
							"Web Project with identification:" + webProject.getIdentification() + " already exists");
				}
			} else {
				throw new WebProjectServiceException("The user is not authorized");
			}
		} else
			throw new WebProjectServiceException("Web project does not exist");
	}

	@Override
	public void deleteWebProject(WebProject webProject, String userId) {
		WebProject wp = this.webProjectRepository.findById(webProject.getId());
		User user = this.userService.getUser(userId);

		if (hasUserPermissionToEditWebProject(user, wp)) {
			deleteFolder(rootFolder + wp.getIdentification() + "/");
			this.webProjectRepository.delete(webProject);
		} else {
			throw new WebProjectServiceException("The user is not authorized");
		}
	}

	@Override
	public void deleteWebProjectById(String id, String userId) {
		WebProject wp = this.webProjectRepository.findById(id);
		User user = this.userService.getUser(userId);

		if (hasUserPermissionToEditWebProject(user, wp)) {
			deleteFolder(rootFolder + wp.getIdentification() + "/");
			this.webProjectRepository.delete(wp);
		} else {
			throw new WebProjectServiceException("The user is not authorized");
		}
	}

	
	@Override
	public void uploadZip(MultipartFile file, String userId) {

		String folder = rootFolder + userId + "/";

		try {
			deleteFolder(folder);
			uploadFileToFolder(file, folder);
			unzipFile(folder, file.getOriginalFilename());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void uploadFileToFolder(MultipartFile file, String path) throws IOException {

		String fileName = file.getOriginalFilename();
		byte[] bytes = file.getBytes();

		InputStream is = new ByteArrayInputStream(bytes);

		File folder = new File(path);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		String fullPath = path + fileName;
		OutputStream os = new FileOutputStream(new File(fullPath));

		IOUtils.copy(is, os);

		is.close();
		os.close();

		log.info(path + fileName + " uploaded");
	}

	private void checkFolder(String path) {
		if (path.contains("..") || path.contains("\\")) {
			throw new WebProjectServiceException("Invalid folder name");
		}
	}

/*	private void deleteFolder(String path) {
		File folder = new File(path);
		if (folder.exists()) {
			folder.delete();
		}
	}
*/	
	
	public static void deleteFolder(String path) {
		File folder = new File(path);
	    File[] files = folder.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolder(f.getAbsolutePath());
	            } else {
	                f.delete();
	            }
	        }
	    }
	    folder.delete();
	}

	private void createFolderWebProject(String identification, String userId) {

		File file = new File(rootFolder + userId + "/");
		if (file.exists() && file.isDirectory()) {
			File newFile = new File(rootFolder + identification + "/");
			if (!file.renameTo(newFile)) {
				throw new WebProjectServiceException(" Cannot create web project folder");
			}
		}

		log.info("new folder created");
	}

	private void updateFolderWebProject(String identification, String userId) {

		File file = new File(rootFolder + userId + "/");
		if (file.exists() && file.isDirectory()) {
			deleteFolder(rootFolder + identification + "/");
			File newFile = new File(rootFolder + identification + "/");
			if (!file.renameTo(newFile)) {
				throw new WebProjectServiceException(" Cannot create web project folder");
			}
		}
	}
	
	private void unzipFile(String path, String fileName) {

		File folder = new File(path + fileName);
		log.info("unzip zip file: " + folder);
		if (folder.exists()) {
			try {
				ZipInputStream zis = new ZipInputStream(new FileInputStream(folder));
				ZipEntry ze;

				while (null != (ze = zis.getNextEntry())) {

					if (ze.isDirectory()) {

						File f = new File(path + ze.getName());
						f.mkdirs();

					} else {
						FileOutputStream fos = new FileOutputStream(path + ze.getName());

						IOUtils.copy(zis, fos);
						log.info("unzip file: " + ze.getName());
						fos.close();
						zis.closeEntry();
					}
				}
				zis.close();

				if (folder.exists()) {
					if (folder.delete()) {
						log.info(folder + "deleted");
					}
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {

		}
	}

	public void zipFile(String folder) {

		File zipFolder = new File(folder);

		if (zipFolder.exists()) {
			File[] files = zipFolder.listFiles();
			log.info("Number of files: " + files.length);

			for (int i = 0; i < files.length; i++) {
				log.info("File name: " + files[i].getName());
				String extension = "";
				for (int j = 0; j < files[i].getName().length(); j++) {
					if (files[i].getName().charAt(j) == '.') {
						extension = files[i].getName().substring(j, (int) files[i].getName().length());
					}
				}
				try {
					ZipOutputStream zous = new ZipOutputStream(
							new FileOutputStream(folder + files[i].getName().replace(extension, ".zip")));

					ZipEntry entry = new ZipEntry(files[i].getName());
					zous.putNextEntry(entry);

					log.info("File name:" + entry.getName());
					FileInputStream fis = new FileInputStream(folder + entry.getName());
					IOUtils.copy(fis, zous);
					fis.close();
					zous.closeEntry();
					zous.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			log.info("Zip FileName: " + folder);
		} else {
			System.out.println("Folder not exist");
		}
	}

}
