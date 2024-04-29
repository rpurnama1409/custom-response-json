package org.acme.resource;

import java.util.List;
import java.util.Optional;

import org.acme.dto.StudentDto;
import org.acme.entity.Student;
import org.acme.repository.StudentRepository;
import org.acme.response.ApiResponse;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("students")
public class StudentResource {
    @Inject
    StudentRepository studentRepository;

    @GET
    @Produces("application/json")
    @Consumes("application/json")
    public ApiResponse<StudentDto> getStudents() {
        List<Student> students = studentRepository.listAll();

        List<StudentDto> data = students.stream()
                .map(student -> new StudentDto(student.getId(), student.getName(), student.getMajor()))
                .toList();
        String message = "success";
        boolean success = true;

        return new ApiResponse<StudentDto>(200, data, message, success);

    }

    @POST
    @Transactional
    @Produces("application/json")
    @Consumes("application/json")

    public Response storeStudent(StudentDto req) {
        Student student = new Student();
        student.setName(req.name());
        student.setMajor(req.major());

        studentRepository.persist(student);
        return Response.ok().build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    @Produces("application/json")
    @Consumes("application/json")

    public Response updateStudent(@PathParam("id") Long id, StudentDto req) {
        Optional<Student> studentOptional = studentRepository.findByIdOptional(id);
        if (studentOptional.isPresent()) {
            Student student = studentOptional.get();
            student.setName(req.name());
            student.setMajor(req.major());

            studentRepository.persist(student);
            return Response.ok().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    @Produces("application/json")

    public Response deleteStudent(@PathParam("id") Long id) {
        Optional<Student> studentOptional = studentRepository.findByIdOptional(id);

        if (studentOptional.isPresent()) {
            Student student = studentOptional.get();
            student.setDeleted(true);

            studentRepository.persist(student);
            return Response.ok().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
