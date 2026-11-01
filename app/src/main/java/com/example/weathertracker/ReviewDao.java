package com.example.weathertracker;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface ReviewDao {

    @Query("SELECT * FROM reviews ORDER BY id DESC")
    List<Review> getAllReviews();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertReview(Review review);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Review> reviews);

    @Update
    void updateReview(Review review);

    @Delete
    void deleteReview(Review review);

    @Query("DELETE FROM reviews WHERE id = :reviewId")
    void deleteById(long reviewId);
}
