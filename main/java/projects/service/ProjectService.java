package projects.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import projects.dao.DbConnection;
import projects.dao.ProjectDao;
import projects.entity.Project;
import projects.exception.DbException;


public class ProjectService {
	  private ProjectDao projectDao = new ProjectDao();
	  /**
	   * This method simply calls the DAO class to insert a project row.
	   * 
	   * @param project The {@link Project} object.
	   * @return The Project object with the newly generated primary key value.
	   */
	  public Project addProject(Project project) {
	    return projectDao.insertProject(project);
	  }
	  /**
	   * This method calls the project DAO to retrieve all project rows without accompanying details
	   * (materials, steps and categories).
	   * 
	   * @return A list of project records.
	   */
	public List<Project> fetchAllProjects() {
		String sql = ("SELECT * FROM project ORDER BY project_name");
		List<Project> projects = new ArrayList<>();
		
		try(Connection conn = DbConnection.getConnection()){
			//c. start a new transaction
			conn.setAutoCommit(false);
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				
				// e. Inner try-with-resources for ResultSet
				try(ResultSet rs = stmt.executeQuery()){
					while(rs.next()) {
						Project project = new Project();
						
						project.setActualHours(rs.getBigDecimal("actual_hours"));
						project.setDifficulty(rs.getObject("difficulty", Integer.class));
						project.setEstimatedHours(rs.getBigDecimal("estimated_hours"));
						project.setNotes(rs.getString("notes"));
						project.setProjectId(rs.getObject("project_id", Integer.class));
						project.setProjectName(rs.getString("project_name"));
						
						projects.add(project);
						
					}

		                // Commit after successful execution
		                conn.commit();
		            }

		        } catch (Exception e) {
		            // d. Rollback and throw DbException
		            conn.rollback();
		            throw new DbException("Error fetching projects", e);
		        }

		    } catch (SQLException e) {
		        // b. Catch SQLException and rethrow as DbException
		        throw new DbException("Unable to establish connection", e);
		    }

		    return projects;
		}
	  /**
	   * This method calls the project DAO to get all project details, including materials, steps, and
	   * categories. If the project ID is invalid, it throws an exception.
	   * 
	   * @param projectId The project ID.
	   * @return A Project object if successful.
	   * @throws NoSuchElementException Thrown if the project with the given ID does not exist.
	   */
	public Project fetchProjectById(Integer projectId) {
	    return projectDao.fetchProjectById(projectId)
	        .orElseThrow(() -> new NoSuchElementException(
	            "Project with project ID=" + projectId + " does not exist."
	        ));
	}
	
	/**
	 * Attempts to update the details of an existing project in the database.
	 * 
	 * @param project The project object containing the updated data.
	 */
	public void modifyProjectDetails(Project project) {
		  // Call the DAO method to perform the update and capture the result
		boolean updated = projectDao.modifyProjectDetails(project);
		
		// If the update failed (e.g., the project ID doesn't exist), throw an exception
		if(!updated) {
			throw new DbException("Project with ID=" + project.getProjectId() + "does not exist.");
		}
		
	}
	
	/**
	 * Attempts to delete a project from the database based on the given project ID.
	 * 
	 * @param projectId The ID of the project to delete.
	 */
	public void deleteProject(Integer projectId) {
		// Call the DAO method to delete the project and check if it succeeded
		if(!projectDao.deleteProject(projectId)) {
			throw new DbException("Project with ID=" + projectId + " does not exist.");
		}
		
		
	}


}
