import React, { useState, useEffect, useReducer, useCallback } from 'react';
// import ls from 'local-storage';
import { v1 as uuid } from 'uuid';
import groupBy from 'lodash.groupby';
import List from './components/List/List';
import { DragDropContext, Droppable, Draggable } from 'react-beautiful-dnd';
import Options from './components/Options/Options';
import { cardsReducer, listsReducer } from './reducers';
import { IList, ICard } from './models';
import { initialCards, initialLists } from './utils';
import { Container, Lists, NewListButton } from './App.styles';
import { reorder } from './utils';
import './styles.css';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities } from './team.reducer';
import { Translate, TextFormat, getPaginationState, JhiPagination, JhiItemCount } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { ITeam } from 'app/shared/model/team.model';
// import uuid from "uuid/v4";
export const TeamKanban = () => {
  const dispatch = useAppDispatch();

  const teamList = useAppSelector(state => state.team.entities);
  const loading = useAppSelector(state => state.team.loading);
  const totalItems = useAppSelector(state => state.team.totalItems);

  // Initialize bgColorFromLs to a default color
  const bgColorFromLs: string = '#ffffff';

  const [bgColor, setBgColor] = useState(bgColorFromLs ? bgColorFromLs : 'dodgerblue');

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  interface IColumn {
    name: string;
    items: any[]; // You can replace `any` with a more specific type if needed
  }

  interface ITeamkanban {
    [key: string]: IColumn;
  }

  // const columnsFromBackend = {
  //   [uuid()]: {
  //     name: "Requested",
  //     items: []
  //   }
  // };

  const [teamKanban, setTeamKanban] = useState<ITeamkanban>({});
  // type TaskItem = {
  //   id: string;
  //   content: string;
  // };

  // type TeamItem = {
  //   id: string;
  //   name: string;
  //   items:TaskItem[]
  // };

  // const columnsFromBackend:TeamItem = [

  //   // {id: uuid(), name:"To do", items:itemsFromBackend},
  //   // {id: uuid(), name:"To do22", items:[]}
  // ]

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const getUserKanbanList = (team: ITeam) => {
    const users: any[] = [];
    team.teamMembers.forEach(user => {
      const task: any = {};
      task.id = uuid();
      task.name = user.login;

      users.push(task);
    });
    return users;
  };
  useEffect(() => {
    const columnsFromBackend = {};
    teamList.forEach(team => {
      columnsFromBackend[uuid()] = {
        name: team.name,
        items: getUserKanbanList(team),
      };
    });

    setTeamKanban(columnsFromBackend);
    setColumns(columnsFromBackend);
    console.log(columnsFromBackend);
  }, [teamList]);

  useEffect(() => {
    getAllEntities();
  }, []);

  const handleBgColorChange = (color: { hex: string }) => {
    setBgColor(color.hex);
    // ls.set<string>('bgColor', color.hex);
  };

  const [columns, setColumns] = useState<ITeamkanban>({});

  const onDragEnd = (result, columns, setColumns) => {
    if (!result.destination) return;
    const { source, destination } = result;

    if (source.droppableId !== destination.droppableId) {
      const sourceColumn = columns[source.droppableId];
      const destColumn = columns[destination.droppableId];
      const sourceItems = [...sourceColumn.items];
      const destItems = [...destColumn.items];
      const [removed] = sourceItems.splice(source.index, 1);
      destItems.splice(destination.index, 0, removed);
      setColumns({
        ...columns,
        [source.droppableId]: {
          ...sourceColumn,
          items: sourceItems,
        },
        [destination.droppableId]: {
          ...destColumn,
          items: destItems,
        },
      });
    } else {
      const column = columns[source.droppableId];
      const copiedItems = [...column.items];
      const [removed] = copiedItems.splice(source.index, 1);
      copiedItems.splice(destination.index, 0, removed);
      setColumns({
        ...columns,
        [source.droppableId]: {
          ...column,
          items: copiedItems,
        },
      });
    }
  };

  return (
    <div style={{ display: 'flex', justifyContent: 'center', height: '100%' }}>
      <DragDropContext onDragEnd={result => onDragEnd(result, columns, setColumns)}>
        {columns
          ? Object.entries(columns).map(([columnId, column], index) => {
              return (
                <div
                  style={{
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                  }}
                  key={columnId}
                >
                  <h2>{column.name}</h2>
                  <div style={{ margin: 8 }}>
                    <Droppable droppableId={columnId} key={columnId}>
                      {(provided, snapshot) => {
                        return (
                          <div
                            {...provided.droppableProps}
                            ref={provided.innerRef}
                            style={{
                              background: snapshot.isDraggingOver ? 'lightblue' : 'lightgrey',
                              padding: 4,
                              width: 250,
                              minHeight: 500,
                            }}
                          >
                            {column.items.map((item, index) => {
                              return (
                                <Draggable key={item.id} draggableId={item.id} index={index}>
                                  {(provided, snapshot) => {
                                    return (
                                      <div
                                        ref={provided.innerRef}
                                        {...provided.draggableProps}
                                        {...provided.dragHandleProps}
                                        style={{
                                          userSelect: 'none',
                                          padding: 16,
                                          margin: '0 0 8px 0',
                                          minHeight: '50px',
                                          backgroundColor: snapshot.isDragging ? '#263B4A' : '#456C86',
                                          color: 'white',
                                          ...provided.draggableProps.style,
                                        }}
                                      >
                                        {item.name}
                                      </div>
                                    );
                                  }}
                                </Draggable>
                              );
                            })}
                            {provided.placeholder}
                          </div>
                        );
                      }}
                    </Droppable>
                  </div>
                </div>
              );
            })
          : []}
      </DragDropContext>
    </div>
  );
};
export default TeamKanban;
// When list deleted, delete all cards with that listId, otherwise loads of cards hang around in localstorage
// Remove anys
//https://codesandbox.io/p/sandbox/jovial-leakey-i0ex5?file=%2Fsrc%2FApp.js%3A88%2C17-139%2C29
//https://dev.to/bnevilleoneill/build-a-beautiful-draggable-kanban-board-with-react-beautiful-dnd-3oij
