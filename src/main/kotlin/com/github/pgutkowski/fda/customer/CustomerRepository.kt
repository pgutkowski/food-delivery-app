package com.github.pgutkowski.fda.customer

import com.github.kittinunf.result.coroutines.SuspendableResult
import com.github.pgutkowski.fda.AsyncDatabase
import com.github.pgutkowski.fda.customer.controller.CreateCustomerRequest
import com.github.pgutkowski.fda.exceptions.NotFoundException
import com.github.pgutkowski.fda.order.OrderTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import java.sql.SQLException
import java.util.*
import javax.annotation.PostConstruct


object CustomerTable : LongIdTable("customer") {
    val uuid = uuid("unique_id").uniqueIndex()

    val firstName = varchar("first_name", 256)

    val lastName = varchar("last_name", 256)

    val phoneNumber = varchar("phone_number", 15)

    val emailAddress = varchar("email_address", 256)

    val address = varchar("address", 1024)
}

class CustomerEntity (id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<CustomerEntity>(CustomerTable)

    var uuid: UUID by CustomerTable.uuid

    var firstName: String by CustomerTable.firstName

    var lastName: String by CustomerTable.lastName

    var phoneNumber: String by CustomerTable.phoneNumber

    var emailAddress: String by CustomerTable.emailAddress

    var address: String by CustomerTable.address

}

fun CustomerEntity.toCustomer() : Customer {
    return Customer(
            uuid = this.uuid,
            firstName = this.firstName,
            lastName = this.lastName,
            phoneNumber = this.phoneNumber,
            address = this.address,
            emailAddress = this.emailAddress
    )
}

@Repository
class CustomerRepository(private val asyncDatabase: AsyncDatabase) {
    @PostConstruct
    fun initializeTable(){
        transaction(asyncDatabase.database) {
            SchemaUtils.createMissingTablesAndColumns(OrderTable)
        }
    }

    suspend fun create(uuid: UUID, createCustomerRequest: CreateCustomerRequest) : SuspendableResult<Customer, SQLException> {
        return SuspendableResult.of {
            asyncDatabase.asyncTransaction {
                CustomerEntity.new {
                    this.uuid = uuid
                    this.firstName = createCustomerRequest.firstName
                    this.lastName = createCustomerRequest.lastName
                    this.phoneNumber = createCustomerRequest.phoneNumber
                    this.emailAddress = createCustomerRequest.emailAddress
                    this.address = createCustomerRequest.address
                }.toCustomer()
            }
        }
    }

    suspend fun findByUuid(uuid: UUID): SuspendableResult<Customer, Exception> {
        return SuspendableResult.of(CustomerEntity.find { CustomerTable.uuid eq uuid }.firstOrNull()?.toCustomer()) {
            NotFoundException(message = "Customer with uuid [$uuid] does not exist")
        }
    }

}