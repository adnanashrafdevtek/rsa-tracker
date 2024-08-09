import styled from 'styled-components';

export const Container = styled.div<{ bgColor: string }>`
  height: 100%;
  flex-direction: column;
  overflow-x: scroll;
  background-color: ${({ bgColor }) => bgColor};
  font-family: sans-serif;
`;

export const Lists = styled.div`
  display: flex;
  align-items: flex-start;
  margin-top: 40px;
`;

export const NewListButton = styled.button`
  min-width: 250px;
  background: rgba(255, 255, 255, 0.2) !important;
  margin: 0 5px;
  height: 30px;
`;
