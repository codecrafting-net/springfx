package net.codecrafting.springfx.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import net.codecrafting.springfx.application.LoginForm;

public class FormValidatorTest 
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void validationModelMustNotBeNull()
	{
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("model must not be null");
		new FormValidator<FormModel>(null);
	}
	
	@Test
	public void instantiation()
	{
		FormModel mockModel = Mockito.mock(FormModel.class);
		FormValidator<FormModel> validator = new FormValidator<FormModel>(mockModel);
		assertNotNull("validation model is null", validator.getModel());
	}
	
	@Test
	public void validatorListenerMustNotBeNull()
	{
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("validationListener must not be null");
		FormModel mockModel = Mockito.mock(FormModel.class);
		FormValidator<FormModel> validator = new FormValidator<FormModel>(mockModel);
		validator.setValidationListener(null);
	}
	
	@Test
	public void validatorListenerCall()
	{
		FormModel mockModel = Mockito.mock(FormModel.class);
		FormValidator<FormModel> validator = new FormValidator<FormModel>(mockModel);
		ValidationListener mockListener = Mockito.mock(ValidationListener.class);
		validator.setValidationListener(mockListener);
		validator.validate();
		Mockito.verify(mockListener, Mockito.times(1)).onValidationSucceeded();
	}
	
	@Test
	public void validatorWithoutListener()
	{
		FormModel mockModel = Mockito.mock(FormModel.class);
		FormValidator<FormModel> validator = new FormValidator<FormModel>(mockModel);
		assertEquals(0, validator.validate().size());	
	}
	
	@Test
	public void validatorWithListener()
	{
		LoginForm model = new LoginForm();
		model.setPass("123");
		model.setUser("springfx");
		FormValidator<LoginForm> validator = new FormValidator<LoginForm>(model);
		ValidationListener mockListener = Mockito.mock(ValidationListener.class);
		validator.setValidationListener(mockListener);
		validator.validate();
		Mockito.verify(mockListener, Mockito.times(1)).onValidationSucceeded();
	}
	
	@Test
	public void validatorWithErrorsWithoutListener()
	{
		LoginForm model = new LoginForm();
		model.setPass("");
		model.setUser("springfx");
		FormValidator<LoginForm> validator = new FormValidator<LoginForm>(model);
		List<ValidationError> errors = validator.validate();
		assertEquals(1, errors.size());
		assertEquals(LoginForm.PASS_FIELD_NAME, errors.get(0).getField());
		assertEquals(LoginForm.PASS_FIELD_MSG, errors.get(0).getMessage());
		assertEquals("", errors.get(0).getValue());
	}
	
	@Test
	public void validatorWithErrorsWithListener()
	{
		LoginForm model = new LoginForm();
		model.setPass("");
		model.setUser("springfx");
		model.setEmail("test");
		FormValidator<LoginForm> validator = new FormValidator<LoginForm>(model);
		ValidationListener mockListener = Mockito.mock(ValidationListener.class);
		Mockito.doAnswer((Answer<Void>) invocation -> {
			List<ValidationError> errors = invocation.getArgument(0);
			assertNotNull(errors);
			assertEquals(2, errors.size());
			List<ValidationError> expectedErrors = Arrays.asList(new ValidationError(LoginForm.PASS_FIELD_NAME, LoginForm.PASS_FIELD_MSG, ""),
					new ValidationError("email", LoginForm.EMAIL_FIELD_MSG, "test"));
			int expectedErrorsSize = expectedErrors.size();
			for (ValidationError expectedError : expectedErrors) {
				for (ValidationError error : errors) {
					if(error.getField().equals(expectedError.getField())) {
						assertEquals(expectedError.getMessage(), error.getMessage());
						assertEquals(expectedError.getValue(), error.getValue());
						expectedErrorsSize--;
						break;
					}
				}
			}
			assertEquals("There are missing errors", expectedErrorsSize, 0);
			return null;
		}).when(mockListener).onValidationFailed(ArgumentMatchers.any());
		validator.setValidationListener(mockListener);
		validator.validate();
		Mockito.verify(mockListener, Mockito.times(1)).onValidationFailed(ArgumentMatchers.any());
	}

	@Test
	public void validatorUpdateCall()
	{
		FormModel mockModel = Mockito.mock(FormModel.class);
		FormValidator<FormModel> validator = new FormValidator<FormModel>(mockModel);
		validator.validate();
		Mockito.verify(mockModel, Mockito.times(1)).setValidation(ArgumentMatchers.any());
	}
}