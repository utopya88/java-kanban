package service;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    void init() {
        taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
    }

    @BeforeEach
    void beforeEach() {
        init();
    }
}