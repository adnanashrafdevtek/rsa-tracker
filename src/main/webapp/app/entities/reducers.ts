import team from 'app/entities/team/team.reducer';
import task from 'app/entities/task/task.reducer';
import message from 'app/entities/message/message.reducer';
import document from 'app/entities/document/document.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  team,
  task,
  message,
  document,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
