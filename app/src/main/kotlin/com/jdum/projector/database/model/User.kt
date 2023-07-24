package com.jdum.projector.database.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "users")
class User(
    @Id
    val id: Int,
    @Column(name = "first_name")
    val firstName: String,
    @Column(name = "date_of_birth")
    val dateOfBirth: LocalDate
){
    override fun toString(): String {
        return "($id,'$firstName','$dateOfBirth')"
    }
}
