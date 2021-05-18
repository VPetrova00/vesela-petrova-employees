import java.util.Date;

public class Employee {
    private final int employeeId;

    private final int projectId;

    private Date dateFrom;

    private Date dateTo;

    public Employee() {
        this.employeeId = 0;
        this.projectId = 0;
        this.dateFrom = new Date();
        this.dateTo = new Date();
    }

    public Employee(int employeeId, int projectId, Date from, Date to) {
        this.employeeId = employeeId;
        this.projectId = projectId;
        this.setDateFrom(from);
        this.setDateTo(to);
    }

    public Employee(int employeeId, int projectId) {
        this.employeeId = employeeId;
        this.projectId = projectId;
    }

    public int getEmployeeId() {
        return this.employeeId;
    }

    public int getProjectId() {
        return this.projectId;
    }

    public Date getDateFrom() {
        return this.dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return this.dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    @Override
    public String toString() {
        return String.format("Employee id: %d", this.employeeId);
    }
}
