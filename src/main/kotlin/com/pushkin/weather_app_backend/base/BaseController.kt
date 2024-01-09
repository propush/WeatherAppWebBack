package com.pushkin.weather_app_backend.base

import com.pushkin.weather_app_backend.security.exception.UserNotAuthorizedException
import com.pushkin.weather_app_backend.user.exception.SignUpException
import com.pushkin.weather_app_backend.user.exception.UserException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.server.ResponseStatusException

open class BaseController {

    private val log = LoggerFactory.getLogger(this::class.java)

    protected fun <T> processServiceExceptions(block: () -> T) =
        try {
            ResponseEntity.ok<T?>(block())
        } catch (e: EntityNotFoundException) {
            log.error("$e")
            e.printStackTrace()
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found: ${e.entity}", e)
        } catch (e: SignUpException) {
            log.error("$e")
            e.printStackTrace()
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "User not authorized", e)
        } catch (e: UserException) {
            log.error("$e")
            e.printStackTrace()
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "User not authorized to run", e)
        } catch (e: UserNotAuthorizedException) {
            log.error("$e")
            e.printStackTrace()
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "User not authorized to run", e)
        } catch (e: Exception) {
            log.error("$e")
            e.printStackTrace()
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred processing the request", e)
        }

}
