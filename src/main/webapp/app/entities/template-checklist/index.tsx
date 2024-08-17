import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TemplateChecklist from './template-checklist';
import TemplateChecklistDetail from './template-checklist-detail';
import TemplateChecklistUpdate from './template-checklist-update';
import TemplateChecklistDeleteDialog from './template-checklist-delete-dialog';

const TemplateChecklistRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TemplateChecklist />} />
    <Route path="new" element={<TemplateChecklistUpdate />} />
    <Route path=":id">
      <Route index element={<TemplateChecklistDetail />} />
      <Route path="edit" element={<TemplateChecklistUpdate />} />
      <Route path="delete" element={<TemplateChecklistDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TemplateChecklistRoutes;
