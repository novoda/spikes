package com.novoda.todoapp.task.newtask.displayer;

import com.google.common.base.Optional;

public interface NewTaskActionListener {
    void save(Optional<String> title, Optional<String> description);
}
