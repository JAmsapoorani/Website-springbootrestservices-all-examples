package com.in.springboot.controller;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import com.in.springboot.StudentApplication;
import com.in.springboot.model.Course;
import com.in.springboot.service.StudentService;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
@ExtendWith(SpringExtension.class)
@WebMvcTest(value = StudentController.class)
@WithMockUser
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = StudentApplication.class,
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerIT {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private StudentService studentService;

	Course mockCourse = new Course("Course1", "Spring", "10 Steps",
			Arrays.asList("Learn Maven", "Import Project", "First Example",
					"Second Example"));
	@LocalServerPort
	private int port;

	TestRestTemplate restTemplate = new TestRestTemplate();

	HttpHeaders headers = new HttpHeaders();

	@BeforeEach
	public void before() {
		headers.add("Authorization", createHttpAuthenticationHeaderValue(
				"user1", "secret1"));
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));
	}

	@Test
	public void testRetrieveStudentCourse() throws Exception {
		HttpEntity<Course> entity = new HttpEntity<>(mockCourse, headers);

		Mockito.when(
				studentService.retrieveCourse(Mockito.anyString(),
						Mockito.anyString())).thenReturn(mockCourse);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
				"/students/Student1/courses/Course1").accept(
				MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		System.out.println(result.getResponse());
		String expected = "{\"id\":\"Course1\",\"name\":\"Spring\",\"description\":\"10 Steps\"}";
		JSONAssert.assertEquals(expected, result.getResponse()
				.getContentAsString(), false);
	}

	@Test
	public void addCourse() {

		Course course = new Course("Course1", "Spring", "10 Steps",
				List.of("Learn Maven", "Import Project", "First Example",
						"Second Example"));

		HttpEntity<Course> entity = new HttpEntity<>(course, headers);

		ResponseEntity<Course> response = restTemplate.exchange(
				createURLWithPort("/students/Student1/courses"),
				HttpMethod.POST, entity,Course.class);

		String actual = response.getHeaders().get(HttpHeaders.LOCATION).get(0);

		assertTrue(actual.contains("http://localhost:8080/students/Student1/courses/"));

	}

	private String createURLWithPort(String uri) {
		return "http://localhost:8080/" + uri;
	}

	private String createHttpAuthenticationHeaderValue(String userId, String password) {
		String auth = userId + ":" + password;
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.US_ASCII));

		return "Basic " + new String(encodedAuth);
	}

}
