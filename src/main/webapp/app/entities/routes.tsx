import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Team from './team';
import Task from './task';
import Message from './message';
import Document from './document';
import TeamKanban from './team/teamKanban';
import TaskKanban from './task/taskKanban';
import Template from './template';
import TemplateTask from './template-task';
import TemplateChecklist from './template-checklist';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="teamKanban/*" element={<TeamKanban />} />
        <Route path="taskKanban/*" element={<TaskKanban />} />
        <Route path="taskKanbanByTeam/*" element={<TaskKanban />} />
        <Route path="team/*" element={<Team />} />
        <Route path="task/*" element={<Task />} />
        <Route path="message/*" element={<Message />} />
        <Route path="document/*" element={<Document />} />
        <Route path="template/*" element={<Template />} />
        <Route path="template-task/*" element={<TemplateTask />} />
        <Route path="template-checklist/*" element={<TemplateChecklist />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
