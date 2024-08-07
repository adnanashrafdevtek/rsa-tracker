import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Team from './team';
import Task from './task';
import Message from './message';
import Document from './document';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="team/*" element={<Team />} />
        <Route path="task/*" element={<Task />} />
        <Route path="message/*" element={<Message />} />
        <Route path="document/*" element={<Document />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
