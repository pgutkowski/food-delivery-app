package com.github.pgutkowski.fda.customer

import com.github.kittinunf.result.coroutines.SuspendableResult
import com.github.pgutkowski.fda.SuspendableDatabase
import com.github.pgutkowski.fda.customer.controller.CreateCustomerRequest
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.UUIDTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import java.util.*
import javax.annotation.PostConstruct


object CustomerTable : UUIDTable("customer") {

    val firstName = varchar("first_name", 256)

    val lastName = varchar("last_name", 256)

    val phoneNumber = varchar("phone_number", 15)

    val emailAddress = varchar("email_address", 256)

    val address = varchar("address", 1024)
}

class CustomerEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<CustomerEntity>(CustomerTable)

    var firstName: String by CustomerTable.firstName

    var lastName: String by CustomerTable.lastName

    var phoneNumber: String by CustomerTable.phoneNumber

    var emailAddress: String by CustomerTable.emailAddress

    var address: String by CustomerTable.address

}

fun CustomerEntity.toCustomer(): Customer {
    return Customer(
            uuid = this.id.value,
            firstName = this.firstName,
            lastName = this.lastName,
            phoneNumber = this.phoneNumber,
            address = this.address,
            emailAddress = this.emailAddress
    )
}

@Repository
class CustomerRepository(private val suspendableDatabase: SuspendableDatabase) {
    @PostConstruct
    fun initializeTable() {
        transaction(suspendableDatabase.database) {
            SchemaUtils.createMissingTablesAndColumns(CustomerTable)
        }
    }

    suspend fun create(createCustomerRequest: CreateCustomerRequest): SuspendableResult<Customer, Exception> {
        return suspendableDatabase.transaction {
            CustomerEntity.new {
                this.firstName = createCustomerRequest.firstName
                this.lastName = createCustomerRequest.lastName
                this.phoneNumber = createCustomerRequest.phoneNumber
                this.emailAddress = createCustomerRequest.emailAddress
                this.address = createCustomerRequest.address
            }.toCustomer()
        }
    }

}