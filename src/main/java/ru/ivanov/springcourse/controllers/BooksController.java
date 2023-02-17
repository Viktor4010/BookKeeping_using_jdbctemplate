package ru.ivanov.springcourse.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.ivanov.springcourse.dao.BookDao;
import ru.ivanov.springcourse.dao.PersonDAO;
import ru.ivanov.springcourse.models.Book;
import ru.ivanov.springcourse.models.Person;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("books")
public class BooksController {

    private final BookDao bookDao;
    private final PersonDAO personDAO;

    @Autowired
    public BooksController(BookDao bookDao, PersonDAO personDAO) {
        this.bookDao = bookDao;
        this.personDAO = personDAO;
    }


    @GetMapping
    public String index(Model model) {
        model.addAttribute("books", bookDao.index());
        return "books/index";
    }

    @GetMapping("{id}")
    public String show(@PathVariable("id") int id, Model model, @ModelAttribute("person") Person person) {
        model.addAttribute("book", bookDao.show(id));

        Optional<Person> bookOwner = bookDao.getBookOwner(id);
        if (bookOwner.isPresent())
            model.addAttribute("owner", bookOwner.get());
        else
            model.addAttribute("people", personDAO.index());

        return "books/show";
    }

    @GetMapping("new")
    public String newBook(@ModelAttribute("book") Book book) {
        return "books/new";
    }

    @PostMapping
    public String create(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "books/new";

        bookDao.save(book);
        return "redirect:/books";
    }

    @GetMapping("{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("book",bookDao.show(id));
        return "books/edit";
    }

    @PatchMapping("{id}")
    public String update(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult,
                         @PathVariable("id") int id) {
        if (bindingResult.hasErrors())
            return "books/edit";

        bookDao.update(id,book);
        return "redirect:/books";
    }

    @DeleteMapping("{id}")
    public String delete(@PathVariable("id") int id) {
        bookDao.delete(id);

        return "redirect:/books";
    }

    @PatchMapping("{id}/release")
    public String release(@PathVariable("id") int id) {
        bookDao.release(id);
        return "redirect:/books/" + id;
    }

    @PatchMapping("{id}/assign")
    public String assign(@PathVariable("id") int id, @ModelAttribute("person") Person selectedPerson) {
        bookDao.assign(id,selectedPerson);
        return "redirect:/books/" + id;
    }
}
