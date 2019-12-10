package com.rar.service;

import com.rar.model.CreateProjectPojo;
import com.rar.model.Projects;
import com.rar.model.UserProjectsPojo;
import java.util.List;

public interface ProjectService {

    Projects projectSave(Projects projects);

    void assign(UserProjectsPojo userProjectsPojo) throws Exception;

    void createProject(CreateProjectPojo createProjectPojo);

    Long getIdByProject(String project_name) throws Exception;

    void deleteUserFromProject(UserProjectsPojo userProjectsPojo);

    Object[] findById(Long project_id);

    Object[] findNotInId(Long project_id);

    List<Projects> findAllData();

    Object[] unAssigned();
}