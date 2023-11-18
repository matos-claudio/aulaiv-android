package com.claudio.aulaiv;

import android.content.Context;

import java.util.List;

public class PersonDAO {
    private DatabaseHelper databaseHelper;

    public PersonDAO(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public long addPerson(Person person) {
        return databaseHelper.addPerson(person);
    }

    public List<Person> getAllPerson() {
        return databaseHelper.getAllPerson();
    }

    public int updatePerson(Person person) {
        return databaseHelper.updatePerson(person);
    }

    public int deletePerson(int personId) {
        return databaseHelper.deletePerson(personId);
    }
}
