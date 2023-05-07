package com.harsh.accidentdetector;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user")
    List<User> getAllUsers();

//
//    @Query(" DELETE FROM user where id NOT IN (SELECT id from user ORDER BY id ASC LIMIT 4  )")
//    void update();

    @Insert
    void insertUser(User... users);

    @Delete
    void delete(User user);


}