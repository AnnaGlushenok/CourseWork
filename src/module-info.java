module com {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.jdi;
    requires java.sql;

    opens com to javafx.fxml;
    exports com;

    opens com.scenes.EconomistMenu.Disability to javafx.fxml;
    exports com.scenes.EconomistMenu.Disability;

    opens com.scenes.AdminMenu to javafx.fxml;
    exports com.scenes.AdminMenu;

    opens com.scenes.EconomistMenu to javafx.fxml;
    exports com.scenes.EconomistMenu;

    opens com.scenes.EconomistMenu.TimeTable to javafx.fxml;
    exports com.scenes.EconomistMenu.TimeTable;

    opens com.scenes.EconomistMenu.Vacations to javafx.fxml;
    exports com.scenes.EconomistMenu.Vacations;

    opens com.scenes.AdminMenu.SetAccountRole to javafx.fxml;
    exports com.scenes.AdminMenu.SetAccountRole;

    opens com.scenes.AdminMenu.DeleteRole to javafx.fxml;
    exports com.scenes.AdminMenu.DeleteRole;

    opens com.scenes.AdminMenu.AddRole to javafx.fxml;
    exports com.scenes.AdminMenu.AddRole;

    opens com.scenes.Modal to javafx.fxml;
    exports com.scenes.Modal;

    opens com.scenes.HumanResourcesDepartmentMenu.AddEmployee to javafx.fxml;
    exports com.scenes.HumanResourcesDepartmentMenu.AddEmployee;

    opens com.scenes.WorkerMenu to javafx.fxml;
    exports com.scenes.WorkerMenu;

    opens com.scenes.WorkerMenu.TimeTable to javafx.fxml;
    exports com.scenes.WorkerMenu.TimeTable;

    opens com.scenes.DirectorMenu to javafx.fxml;
    exports com.scenes.DirectorMenu;

    opens com.scenes.DirectorMenu.EmployeeStatistics to javafx.fxml;
    exports com.scenes.DirectorMenu.EmployeeStatistics;

    opens com.scenes.DirectorMenu.Vacations to javafx.fxml;
    exports com.scenes.DirectorMenu.Vacations;

    opens com.scenes.DirectorMenu.Statistic to javafx.fxml;
    exports com.scenes.DirectorMenu.Statistic;

    opens com.scenes.HumanResourcesDepartmentMenu.Position to javafx.fxml;
    exports com.scenes.HumanResourcesDepartmentMenu.Position;

    opens com.scenes.HumanResourcesDepartmentMenu to javafx.fxml;
    exports com.scenes.HumanResourcesDepartmentMenu;

    opens com.scenes.HumanResourcesDepartmentMenu.SetPosition to javafx.fxml;
    exports com.scenes.HumanResourcesDepartmentMenu.SetPosition;

    opens com.scenes.HumanResourcesDepartmentMenu.DeleteEmployee to javafx.fxml;
    exports com.scenes.HumanResourcesDepartmentMenu.DeleteEmployee;

    opens com.scenes.WorkerMenu.Vacations to javafx.fxml;
    exports com.scenes.WorkerMenu.Vacations;

    opens com.assets.services to javafx.fxml;
    exports com.assets.services;
}