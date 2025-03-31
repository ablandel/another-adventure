package github.ablandel.anotheradventure.config

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.lang.NonNull
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.util.stream.Collectors

@RestControllerAdvice
class ExceptionControllerAdvice : ResponseEntityExceptionHandler() {
    private fun formatObjectError(error: ObjectError): String =
        when (error) {
            is FieldError -> "`${error.field}` ${error.defaultMessage}"
            else -> error.objectName
        }

    private fun createCustomResponseEntity(message: String): ResponseEntity<Any> {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(problemDetail)
    }

    override fun handleMethodArgumentNotValid(
        @NonNull ex: MethodArgumentNotValidException,
        @NonNull headers: HttpHeaders,
        @NonNull status: HttpStatusCode,
        @NonNull request: WebRequest,
    ): ResponseEntity<Any> {
        val errorsAsSingleString =
            ex.bindingResult
                .allErrors
                .stream()
                .map { error: ObjectError -> this.formatObjectError(error) }
                .collect(Collectors.joining(" - "))
        return createCustomResponseEntity(errorsAsSingleString)
    }

    override fun handleHttpMessageNotReadable(
        @NonNull ex: HttpMessageNotReadableException,
        @NonNull headers: HttpHeaders,
        @NonNull status: HttpStatusCode,
        @NonNull request: WebRequest,
    ): ResponseEntity<Any>? =
        when (val cause = ex.cause) {
            is InvalidFormatException -> {
                val keyInError = cause.value.toString()
                createCustomResponseEntity(String.format(INVALID_FORMAT_GENERIC_ERROR, keyInError))
            }

            is MismatchedInputException -> {
                val regex = Regex("JSON property (\\w+)")
                val result = regex.find(cause.message.toString())?.groupValues
                if (result != null && result.size > 1) {
                    createCustomResponseEntity(String.format(MISMATCHED_INPUT_ERROR, result[1]))
                } else {
                    super.handleHttpMessageNotReadable(ex, headers, status, request)
                }
            }

            else -> super.handleHttpMessageNotReadable(ex, headers, status, request)
        }

    companion object {
        private const val INVALID_FORMAT_GENERIC_ERROR =
            "Cannot deserialize the `%s` value"
        private const val MISMATCHED_INPUT_ERROR =
            "Invalid value(s) provided in `%s`"
    }
}
