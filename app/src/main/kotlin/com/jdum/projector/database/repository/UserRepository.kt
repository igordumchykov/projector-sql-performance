package com.jdum.projector.database.repository

import com.jdum.projector.database.model.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<User, Int> {
}
