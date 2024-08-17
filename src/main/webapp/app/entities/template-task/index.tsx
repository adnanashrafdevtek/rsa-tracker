import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TemplateTask from './template-task';
import TemplateTaskDetail from './template-task-detail';
import TemplateTaskUpdate from './template-task-update';
import TemplateTaskDeleteDialog from './template-task-delete-dialog';

const TemplateTaskRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TemplateTask />} />
    <Route path="new" element={<TemplateTaskUpdate />} />
    <Route path=":id">
      <Route index element={<TemplateTaskDetail />} />
      <Route path="edit" element={<TemplateTaskUpdate />} />
      <Route path="delete" element={<TemplateTaskDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TemplateTaskRoutes;
