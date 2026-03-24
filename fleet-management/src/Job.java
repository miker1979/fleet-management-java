public class Job {

    private int jobId;
    private String jobName;
    private String contractingCompany;
    private String location;
    private String startDate;
    private String estimatedCompletionDate;
    private String status;
    private String projectManager;
    private String notes;

    public Job(int jobId, String jobName, String contractingCompany, String location,
               String startDate, String estimatedCompletionDate,
               String status, String projectManager, String notes) {

        this.jobId = jobId;
        this.jobName = jobName;
        this.contractingCompany = contractingCompany;
        this.location = location;
        this.startDate = startDate;
        this.estimatedCompletionDate = estimatedCompletionDate;
        this.status = status;
        this.projectManager = projectManager;
        this.notes = notes;
    }

    public void displayJob() {
        System.out.println("Job ID: " + jobId);
        System.out.println("Job Name: " + jobName);
        System.out.println("Contracting Company: " + contractingCompany);
        System.out.println("Location: " + location);
        System.out.println("Start Date: " + startDate);
        System.out.println("Estimated Completion: " + estimatedCompletionDate);
        System.out.println("Status: " + status);
        System.out.println("Project Manager: " + projectManager);
        System.out.println("Notes: " + notes);
        System.out.println("---------------------------");
    }

    public int getJobId() {
        return jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}