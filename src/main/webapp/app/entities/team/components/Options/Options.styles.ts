import styled from 'styled-components';

export const Container = styled.div`
  display: flex;
  position: fixed;
  left: 0;
  right: 0;
`;

export const MenuButton = styled.button`
  right: 0;
  position: absolute;

  :hover {
    background: rgba(0, 0, 0, 0.05);
  }
`;

export const CirclePickerContainer = styled.div`
  background: white;
  position: absolute;
  padding: 10px;
  box-sizing: border-box;
  right: 0;
  top: 20px;
  box-shadow: 0px 0px 10px 5px rgba(0, 0, 0, 0.1);
`;
