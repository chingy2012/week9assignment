package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {
	  private Scanner scanner = new Scanner(System.in);
	  private ProjectService projectService = new ProjectService();
	// Holds the currently selected project. This is updated when a user selects or updates a project.
	  private Project curProject;
	// @formatter:off
	  private List<String> operations = List.of(
			  "1) Add a project",
		      "2) List projects",
		      "3) Select a project",
		      "4) Update project details",
		      "5) Delete a project"
	  );
	// @formatter:on
	  
	  /**
	   * Entry point for Java application.
	   * 
	   * @param args Unused.
	   */

	  public static void main(String[] args) {
		    // Launches the application by creating a ProjectsApp instance and starting the main loop.
	    new ProjectsApp().processUserSelections();
	  }
	  
	  /**
	   * Main application loop that presents the user menu, receives user input, 
	   * and executes the corresponding operation.
	   */

	  private void processUserSelections() {
	    boolean done = false;
	 // Continue showing the menu until the user chooses to exit.
	    while(!done) {
	      try {
	        int selection = getUserSelection();

	        switch(selection) {
	          case -1:
	        	// Exit the application
	            done = exitMenu();
	            break;

	          case 1:
	        	// Create a new project
	            createProject();
	            break;
	            
	          case 2:
	        	// List all available projects
	              listProjects();
	              break;
	              
	          case 3:
	        	// Select a project to work with
	        	  selectProject();
	        	  break;
	        	  
	          case 4:
	        	// Update the currently selected project's details
	        	  updateProjectDetails();
	        	  break;
	        
	          case 5:
	        	  // Delete a project
	        	  deleteProject();
	        	  break;

	          default:
	        	// Handle invalid input
	            System.out.println("\n" + selection + " is not a valid selection. Try again.");
	            break;
	        }
	      }
	      catch(Exception e) {
	    	// Handle unexpected input or runtime errors gracefully
	        System.out.println("\nError: " + e + " Try again.");
	      }
	    }
	  }
	  
	  /**
	   * Prompts the user to select a project ID to delete, performs the deletion,
	   * and clears the current project if it matches the deleted one.
	   */
	  private void deleteProject() {
		  listProjects();// Display the list of projects to choose from
		  Integer projectId = getIntInput("Enter the ID of the project to delete ");
		  
		  projectService.deleteProject(projectId);// Delete the project by ID
		  System.out.println("Project " + projectId + " was deleted successfully.");
		  
		// If the deleted project was the currently selected one, reset curProject
			  if(Objects.nonNull(curProject) && curProject.getProjectId().equals(projectId)) {
			  curProject = null;
			  }
	}
	  /**
	   * Updates details of the currently selected project by prompting the user for new values.
	   * Fields left blank by the user will retain their current values.
	   */
	private void updateProjectDetails() {
		 // Ensure a project is selected before proceeding
		if(Objects.isNull(curProject)) {
			System.out.println("\nPlease select a project.");
			return;
		}
		
		// Prompt user for each field, showing current value as default
		String projectName = getStringInput("Enter the project name [" + curProject.getProjectName() + "]"); 
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours [" + curProject.getEstimatedHours() + "]");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours [" + curProject.getActualHours() + "]");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5) [" + curProject.getDifficulty() + "]");
		String notes = getStringInput("Enter the project notes [" + curProject.getNotes() + "]");
		
		// Create a new Project object and assign updated values (use existing if input is null)
		Project project = new Project();
		project.setProjectName(Objects.isNull(projectName)
			? curProject.getProjectName() : projectName); // A ? is a ternary is an if statement in 1 line. (simplified if statement.
		project.setEstimatedHours(Objects.isNull(estimatedHours)
			? curProject.getEstimatedHours() : estimatedHours);
		project.setActualHours(Objects.isNull(actualHours)
			? curProject.getActualHours() : actualHours);
		project.setDifficulty(Objects.isNull(difficulty)
			? curProject.getDifficulty() : difficulty);
		project.setNotes(Objects.isNull(notes)
			? curProject.getNotes() : notes);
		project.setProjectId(curProject.getProjectId());// Ensure the project ID is preserved
		
		// Call the service method to update the project in the DB
		projectService.modifyProjectDetails(project);
		
		// Refresh the current project with updated info from the database
		curProject = projectService.fetchProjectById(curProject.getProjectId());
		System.out.println("Project successfully updated.");
	}

	/**
	 * Gathers user input to create a new project and calls the project service
	 * to insert the project into the database. Displays the result after creation.
	 */

	  private void createProject() {
		  // Prompt user for project name
	    String projectName = getStringInput("Enter the project name");
	 // Prompt user for estimated hours to complete the project
	    BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
	 // Prompt user for actual hours spent (if known)
	    BigDecimal actualHours = getDecimalInput("Enter the actual hours");
	 // Prompt user for difficulty level (1 to 5)
	    Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
	 // Prompt user for any additional project notes
	    String notes = getStringInput("Enter the project notes");
	 // Create a new Project object and populate it with the user inputs
	    Project project = new Project();

	    project.setProjectName(projectName);
	    project.setEstimatedHours(estimatedHours);
	    project.setActualHours(actualHours);
	    project.setDifficulty(difficulty);
	    project.setNotes(notes);

	    // Pass the project to the service layer to be saved in the database
	    Project dbProject = projectService.addProject(project);

	    // Display confirmation message with the created project details
	    System.out.println("You have successfully created project: " + dbProject);
	  }
	  
	  /**
	   * This method calls the project service to retrieve a list of projects from the projects table.
	   * It then uses a Lambda expression to print the project IDs and names on the console. 
	   */
	  
	  private void listProjects() {
		  List<Project> projects = projectService.fetchAllProjects();
		  
		  System.out.println("\nProjects:");
		  
		  projects.forEach(project -> System.out.println("  " + project.getProjectId() + ": " + project.getProjectName()));
	  }
	  
	  /**
	   * This method allows the user to select a "current" project. The current project is one on which
	   * you can add materials, steps, and categories.
	   */
	  private void selectProject() {
		  listProjects();
		  Integer projectId = getIntInput("Enter a project ID to select a project");
		  
		    /*
		     * Unselect the current project. This must be done as a pre-step to fetching the project because
		     * fetchProjectById() will throw an exception if an invalid project ID is entered, which would
		     * leave the currently selected project intact.
		     */
		  curProject = null;
		  
		  /*This will throw an exception if an invalid project ID is entered. */
		  curProject = projectService.fetchProjectById(projectId);
	  }

	  /**
	   * Gets the user's input from the console and converts it to a BigDecimal.
	   * 
	   * @param prompt The prompt to display on the console.
	   * @return A BigDecimal value if successful.
	   * @throws DbException Thrown if an error occurs converting the number to a BigDecimal.
	   */
	  private BigDecimal getDecimalInput(String prompt) {
	    String input = getStringInput(prompt);

	    if(Objects.isNull(input)) {
	      return null;
	    }

	    try {
	    	/* Create the BigDecimal object and set it to two decimal places (the scale). */
	      return new BigDecimal(input).setScale(2);
	    }
	    catch(NumberFormatException e) {
	      throw new DbException(input + " is not a valid decimal number.");
	    }
	  }
	  
	  /**
	   * Called when the user wants to exit the application. It prints a message and returns
	   * {@code true} to terminate the app.
	   * 
	   * @return {@code true}
	   */

	  private boolean exitMenu() {
	    System.out.println("Exiting the menu.");
	    return true;
	  }
	  /**
	   * This method prints the available menu selections. It then gets the user's menu selection from
	   * the console and converts it to an int.
	   * 
	   * @return The menu selection as an int or -1 if nothing is selected.
	   */

	  private int getUserSelection() {
	    printOperations();

	    Integer input = getIntInput("Enter a menu selection");

	    return Objects.isNull(input) ? -1 : input;
	  }
	  /**
	   * Prints a prompt on the console and then gets the user's input from the console. It then
	   * converts the input to an Integer.
	   * 
	   * @param prompt The prompt to print.
	   * @return If the user enters nothing, {@code null} is returned. Otherwise, the input is converted
	   *         to an Integer.
	   * @throws DbException Thrown if the input is not a valid Integer.
	   */

	  private Integer getIntInput(String prompt) {
	    String input = getStringInput(prompt);

	    if(Objects.isNull(input)) {
	      return null;
	    }

	    try {
	      return Integer.valueOf(input);
	    }
	    catch(NumberFormatException e) {
	      throw new DbException(input + " is not a valid number.");
	    }
	  }

	  /**
	   * Prints a prompt on the console and then gets the user's input from the console. If the user
	   * enters nothing, {@code null} is returned. Otherwise, the trimmed input is returned.
	   * 
	   * @param prompt The prompt to print.
	   * @return The user's input or {@code null}.
	   */
	  private String getStringInput(String prompt) {
	    System.out.print(prompt + ": ");
	    String input = scanner.nextLine();

	    return input.isBlank() ? null : input.trim();
	  }
	  
	  /**
	   * Print the menu selections, one per line.
	   */


	  private void printOperations() {
	    System.out.println("\nThese are the available selections. Press the Enter key to quit:");

	    /* With Lambda expression */
	    operations.forEach(line -> System.out.println("  " + line));
	    
	    /* With enhanced for loop */
	    // for(String line : operations) {
	    // System.out.println(" " + line);
	    // }
	    
	    if(Objects.isNull(curProject)) {
	    	System.out.println("\nYou are not working with a project.");
	    } else {
	    	System.out.println("\nYou are working with project: " + curProject);
	    }

	  }
}

