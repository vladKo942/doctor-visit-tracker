package doctorVisitTracker.dto;

import doctorVisitTracker.entity.Doctor;

public record DoctorResponse(String firstName,
                             String lastName,
                             int totalPatients) {

    public static DoctorResponse from(Doctor doctor, int totalPatients) {
        return new DoctorResponse(doctor.getFirstName(), doctor.getLastName(), totalPatients);
    }
}
