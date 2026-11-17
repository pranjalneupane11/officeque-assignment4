package edu.ysu.officeque.web;

/** Temporary test identities; the complete OfficeQue project will use persistent accounts. */
public final class DemoIdentity {
    private DemoIdentity() { }

    public static long studentId(String username) {
        return switch (username) {
            case "student1" -> 1L;
            case "student2" -> 2L;
            default -> throw new SecurityException("Unrecognized student account");
        };
    }

    public static long facultyId(String username) {
        return switch (username) {
            case "faculty1" -> 1L;
            default -> throw new SecurityException("Unrecognized faculty account");
        };
    }
}
