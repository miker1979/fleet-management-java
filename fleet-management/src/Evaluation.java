public class Evaluation {

    private int evaluationId;
    private int employeeId;
    private String evaluationType; // Monthly, Quarterly, Yearly
    private String evaluationDate;
    private String evaluatorName;

    // Ratings (1–5)
    private int jobKnowledge;
    private int workQuality;
    private int attendance;
    private int productivity;
    private int communication;
    private int dependability;

    // Comments
    private String jobKnowledgeComments;
    private String workQualityComments;
    private String attendanceComments;
    private String productivityComments;
    private String communicationComments;
    private String dependabilityComments;

    public Evaluation(int evaluationId, int employeeId, String evaluationType,
                      String evaluationDate, String evaluatorName,
                      int jobKnowledge, int workQuality, int attendance,
                      int productivity, int communication, int dependability) {

        this.evaluationId = evaluationId;
        this.employeeId = employeeId;
        this.evaluationType = evaluationType;
        this.evaluationDate = evaluationDate;
        this.evaluatorName = evaluatorName;

        // Validate ratings (1–5)
        this.jobKnowledge = validateRating(jobKnowledge);
        this.workQuality = validateRating(workQuality);
        this.attendance = validateRating(attendance);
        this.productivity = validateRating(productivity);
        this.communication = validateRating(communication);
        this.dependability = validateRating(dependability);
    }

    // 🔒 Rating validation
    private int validateRating(int rating) {
        if (rating < 1) return 1;
        if (rating > 5) return 5;
        return rating;
    }

    // SET COMMENTS
    public void setComments(String job, String work, String attend,
                            String prod, String comm, String depend) {

        this.jobKnowledgeComments = job;
        this.workQualityComments = work;
        this.attendanceComments = attend;
        this.productivityComments = prod;
        this.communicationComments = comm;
        this.dependabilityComments = depend;
    }

    // GETTERS (important for UI later)
    public int getEvaluationId() { return evaluationId; }
    public int getEmployeeId() { return employeeId; }
    public String getEvaluationType() { return evaluationType; }
    public String getEvaluationDate() { return evaluationDate; }
    public String getEvaluatorName() { return evaluatorName; }

    public int getJobKnowledge() { return jobKnowledge; }
    public int getWorkQuality() { return workQuality; }
    public int getAttendance() { return attendance; }
    public int getProductivity() { return productivity; }
    public int getCommunication() { return communication; }
    public int getDependability() { return dependability; }

    public String getJobKnowledgeComments() { return jobKnowledgeComments; }
    public String getWorkQualityComments() { return workQualityComments; }
    public String getAttendanceComments() { return attendanceComments; }
    public String getProductivityComments() { return productivityComments; }
    public String getCommunicationComments() { return communicationComments; }
    public String getDependabilityComments() { return dependabilityComments; }

    // CALCULATE OVERALL SCORE
    public double getOverallScore() {
        int total = jobKnowledge + workQuality + attendance +
                    productivity + communication + dependability;

        return total / 6.0;
    }

    // DISPLAY
    public void displayEvaluation() {
        System.out.println("Evaluation ID: " + evaluationId);
        System.out.println("Employee ID: " + employeeId);
        System.out.println("Type: " + evaluationType);
        System.out.println("Date: " + evaluationDate);
        System.out.println("Evaluator: " + evaluatorName);

        System.out.println("Job Knowledge: " + jobKnowledge);
        System.out.println("Work Quality: " + workQuality);
        System.out.println("Attendance: " + attendance);
        System.out.println("Productivity: " + productivity);
        System.out.println("Communication: " + communication);
        System.out.println("Dependability: " + dependability);

        System.out.println("Overall Score: " + getOverallScore());

        System.out.println("---- Comments ----");
        System.out.println("Job Knowledge: " + jobKnowledgeComments);
        System.out.println("Work Quality: " + workQualityComments);
        System.out.println("Attendance: " + attendanceComments);
        System.out.println("Productivity: " + productivityComments);
        System.out.println("Communication: " + communicationComments);
        System.out.println("Dependability: " + dependabilityComments);

        System.out.println("----------------------------");
    }
}