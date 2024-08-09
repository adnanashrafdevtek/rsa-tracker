import React, { useState, FunctionComponent } from 'react';
import { Container, MenuButton, CirclePickerContainer } from './Options.styles';
import { CirclePicker } from 'react-color';

interface IOptionsProps {
  handleBgColorChange: any;
}

const Options: FunctionComponent<IOptionsProps> = ({ handleBgColorChange }) => {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <Container>
      <MenuButton onClick={() => setSidebarOpen(!sidebarOpen)}>... Choose theme</MenuButton>
      {sidebarOpen && (
        <CirclePickerContainer>
          <CirclePicker
            onChangeComplete={color => {
              handleBgColorChange(color);
              setSidebarOpen(false);
            }}
          />
        </CirclePickerContainer>
      )}
    </Container>
  );
};

export default Options;
