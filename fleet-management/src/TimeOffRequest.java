public class TimeOffRequest {

    private int requestId;
    private int employeeId;
    private String employeeName;
    private String startDate;
    private String endDate;
    private String requestType;
    private String reason;
    private String status;

    public TimeOffRequest(int requestId, int employeeId, String employeeName,
                          String startDate, String endDate,
                          String requestType, String reason, String status) {
        this.requestId = requestId;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.requestType = requestType;
        this.reason = reason;
        this.status = status;
    }

    public int getRequestId() {
        return requestId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getRequestType() {
        return requestType;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void displayRequest() {
        System.out.println("Request ID: " + requestId);
        System.out.println("Employee ID: " + employeeId);
        System.out.println("Employee Name: " + employeeName);
        System.out.println("Start Date: " + startDate);
        System.out.println("End Date: " + endDate);
        System.out.println("Request Type: " + requestType);
        System.out.println("Reason: " + reason);
        System.out.println("Status: " + status);
        System.out.println("----------------------------");
    }

    @Override
    public String toString() {
        return "Request #" + requestId + " | " + employeeName + " | " +
               startDate + " to " + endDate + " | " + requestType + " | " + status;
    }
}