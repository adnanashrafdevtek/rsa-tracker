import React from 'react';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/team">
        Team
      </MenuItem>
      <MenuItem icon="asterisk" to="/task">
        Task
      </MenuItem>
      <MenuItem icon="asterisk" to="/message">
        Message
      </MenuItem>
      <MenuItem icon="asterisk" to="/document">
        Document
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
