import styled from 'styled-components';

export const Container = styled.div`
  background: white;
  margin-top: 5px;
  display: flex;
  justify-content: space-between;
  user-select: none;
`;

export const Left = styled.div`
  display: flex;
`;

export const Right = styled.div`
  margin-left: auto;
  display: flex;
`;

export const SaveButton = styled.button`
  background: mediumseagreen;
  color: white;

  :hover {
    background: lightseagreen;
  }
`;
