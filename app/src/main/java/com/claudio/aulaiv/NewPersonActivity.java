package com.claudio.aulaiv;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class NewPersonActivity extends AppCompatActivity {

    private PersonDAO personDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_person);

        personDAO = new PersonDAO(this);

        Person person = new Person(1, "Allan");
//        long id = personDAO.addPerson(person);
//        System.out.println("ID DO ALLAN: "+ id);

//        List<Person> personList = personDAO.getAllPerson();

//        for (Person p: personList) {
//            System.out.println("ID: " + p.getId());
//            System.out.println("NOME: " + p.getName());
//        }

        Person person1 = new Person(3, "Vitor");
        int rowsAffected = personDAO.updatePerson(person1);
        System.out.println("LINHAS AFETADAS: " + rowsAffected);

        int lines = personDAO.deletePerson(1);
        System.out.println("Linhas afetadas " + lines);

        List<Person> personList = personDAO.getAllPerson();

        for (Person p: personList) {
            System.out.println("ID: " + p.getId());
            System.out.println("NOME: " + p.getName());
        }
    }
}