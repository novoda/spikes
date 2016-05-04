package com.novoda.todoapp.task.edit.displayer;

import com.google.common.base.Optional;

public interface TaskEditActionListener {

    void save(Optional<String> title, Optional<String> description);

}
