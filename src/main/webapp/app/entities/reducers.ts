import team from 'app/entities/team/team.reducer';
import task from 'app/entities/task/task.reducer';
import message from 'app/entities/message/message.reducer';
import document from 'app/entities/document/document.reducer';
import template from 'app/entities/template/template.reducer';
import templateTask from 'app/entities/template-task/template-task.reducer';
import templateChecklist from 'app/entities/template-checklist/template-checklist.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  team,
  task,
  message,
  document,
  template,
  templateTask,
  templateChecklist,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
