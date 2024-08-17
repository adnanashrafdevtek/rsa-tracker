import React, { useState, useEffect, useReducer, useCallback } from 'react';
// import ls from 'local-storage';
import { v1 as uuid } from 'uuid';
import groupBy from 'lodash.groupby';
// import List from './components/List/List';
import { DragDropContext, Droppable, Draggable } from 'react-beautiful-dnd';
// import Options from './components/Options/Options';
// import { cardsReducer, listsReducer } from './reducers';
// import { IList, ICard } from './models';
// import { initialCards, initialLists } from './utils';
// import { Container, Lists, NewListButton } from './App.styles';
// import { reorder } from './utils';
// import './styles.css';

// import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities } from './task.reducer';
import { getEntities as getTeams } from '../team/team.reducer';
import { getUsers } from '../../modules/administration/user-management/user-management.reducer';
import { Translate, TextFormat, getPaginationState, JhiPagination, JhiItemCount, ValidatedField } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import './styles.css';
import { TaskStatus } from 'app/shared/model/enumerations/task-status.model';
import MyCard from './CardComponent';
import { Button } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
const taskStatusValues = Object.keys(TaskStatus);

export const TaskKanban = () => {
  const dispatch = useAppDispatch();

  const list = useAppSelector(state => state.task.entities);
  const loading = useAppSelector(state => state.task.loading);
  const totalItems = useAppSelector(state => state.task.totalItems);

  const teamList = useAppSelector(state => state.team.entities);
  const userList = useAppSelector(state => state.userManagement.users);

  // Initialize bgColorFromLs to a default color
  const bgColorFromLs: string = '#ffffff';
  const [teams, setTeams] = useState(teamList);
  const [users, setUsers] = useState(teamList);
  const [bgColor, setBgColor] = useState(bgColorFromLs ? bgColorFromLs : 'dodgerblue');
  const [showBy, setShowBy] = useState('STATUS');
  const pageLocation = useLocation();
  const navigate = useNavigate();
  const [teamCheckboxList, setTeamCheckboxList] = useState([]);
  const [userCheckboxList, setUserCheckboxList] = useState([]);
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  interface IColumn {
    [x: string]: any;
    name: string;
    items: any[]; // You can replace `any` with a more specific type if needed
  }

  interface Ikanban {
    [key: string]: IColumn;
  }

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const getTasks = (list: Ikanban[]) => {
    const tasks: any[] = [];
    list.forEach(item => {
      const task: any = {};
      task.id = uuid();
      task.taskId = item.id;
      task.name = item.name;
      task.priority = item.priority;
      task.dueDate = item.dueDate;
      task.assignedTo = item.assignedTo?.login;
      task.team = item.team?.name;
      tasks.push(task);
    });
    return tasks;
  };

  useEffect(() => {
    setTeams(teamList);
  }, [teamList]);

  useEffect(() => {
    setUsers(userList);
  }, [userList]);

  const showByStatus = () => {
    setShowBy('STATUS');
    const columnsFromBackend = {};

    taskStatusValues.map(
      taskStatus =>
        (columnsFromBackend[uuid()] = {
          name: taskStatus,
          items: getTasks(list.filter(item => item.status === taskStatus)),
        }),
    );
    setColumns(columnsFromBackend);
    console.log(columnsFromBackend);
  };
  const showByTeams = () => {
    setShowBy('TEAM');

    const columnsFromBackend = {};

    teams.map(
      team =>
        (columnsFromBackend[uuid()] = {
          name: team.name,
          items: getTasks(list.filter(item => item.team?.id === team.id)),
        }),
    );
    setColumns(columnsFromBackend);
    console.log(columnsFromBackend);
  };

  useEffect(() => {
    if (showBy === 'STATUS') showByStatus();
    else showByTeams();
  }, [list]);

  useEffect(() => {
    getAllEntities();
    dispatch(
      getTeams({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );

    dispatch(
      getUsers({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  }, []);

  useEffect(() => {
    console.log('team=' + teamCheckboxList);
    console.log('user=' + userCheckboxList);
    onTeamChange(teamCheckboxList.join(','), userCheckboxList.join(','));
  }, [teamCheckboxList, userCheckboxList]);

  const onTeamCheckboxChange = checkbox => {
    console.log('onTeamCheckboxChange--------------');
    console.log(checkbox);
    console.log(checkbox.currentTarget.checked);
    console.log(checkbox.currentTarget.value);

    const checked = checkbox.currentTarget.checked;
    const id = checkbox.currentTarget.value;

    if (checked === true) {
      if (!teamCheckboxList.includes(id)) {
        teamCheckboxList.push(id);
        setTeamCheckboxList(teamCheckboxList);
        onTeamChange(teamCheckboxList.join(','), userCheckboxList.join(','));
      }
    } else {
      const filteredList = teamCheckboxList.filter(index => index !== id);
      setTeamCheckboxList(filteredList);
      onTeamChange(filteredList.join(','), userCheckboxList.join(','));
    }
  };

  const onUserCheckboxChange = checkbox => {
    console.log('onUserCheckboxChange--------------');
    console.log(checkbox);
    console.log(checkbox.currentTarget.checked);
    console.log(checkbox.currentTarget.value);

    const checked = checkbox.currentTarget.checked;
    const id = checkbox.currentTarget.value;

    if (checked === true) {
      if (!userCheckboxList.includes(id)) {
        userCheckboxList.push(id);
        setUserCheckboxList(userCheckboxList);
        onTeamChange(teamCheckboxList.join(','), userCheckboxList.join(','));
      }
    } else {
      const filteredList = userCheckboxList.filter(index => index !== id);
      setUserCheckboxList(filteredList);
      onTeamChange(teamCheckboxList.join(','), filteredList.join(','));
    }
  };

  const onTeamChange = (team, user) => {
    console.log('===========on change==================teams=' + team + ', users=' + user);
    let query = '';
    if (team !== '') query = 'teamId.in=' + team;

    if (user !== '') {
      if (team === '') query = 'assignedToId.in=' + user;
      else query = query + '&assignedToId.in=' + user;
    }

    dispatch(
      getEntities({
        query: query,
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };
  const handleBgColorChange = (color: { hex: string }) => {
    setBgColor(color.hex);
    // ls.set<string>('bgColor', color.hex);
  };

  const [columns, setColumns] = useState<Ikanban>({});

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
  //<button onClick={showByTeams}>Show Tasks By Teams</button><button onClick={showByStatus}>Show Tasks By Status</button>
  //<button onClick={showByTeams}>Show Tasks By Teams</button><button onClick={showByStatus}>Show Tasks By Status</button>
  return (
    <div className="d-flex p-2 bd-highlight">
      <div>
        <Button
          onClick={() =>
            (window.location.href = `/task/2/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
          }
          color="danger"
          size="sm"
          data-cy="entityDeleteButton"
        >
          <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Delete</span>
        </Button>
        <div>
          {' '}
          <button onClick={showByTeams}>Show Tasks By Teams</button>
        </div>
        <div>
          <button onClick={showByStatus}>Show Tasks By Status</button>
        </div>
        <b>Search By Teams</b>
        <div className="d-flex justify-content-center">
          <ul>
            <li key="0">
              <div className="toppings-list-item">
                <div className="left-section">
                  <input
                    type="checkbox"
                    id={`team-checkbox-0`}
                    value="0"
                    defaultChecked={true}
                    onChange={e => {
                      onTeamCheckboxChange(e);
                    }}
                  />
                  <label htmlFor={`team-checkbox-0`}>All</label>
                </div>
              </div>
            </li>
            {teams.map(({ id, name }, index) => {
              return (
                <li key={index}>
                  <div className="toppings-list-item">
                    <div className="left-section">
                      <input
                        type="checkbox"
                        id={`team-checkbox-${index}`}
                        name={name}
                        value={id}
                        onChange={e => {
                          onTeamCheckboxChange(e);
                        }}
                      />
                      <label htmlFor={`team-checkbox-${index}`}>{name}</label>
                    </div>
                  </div>
                </li>
              );
            })}
          </ul>
        </div>
        <div>
          <b>Search By Users</b>
          <ul>
            <li key="0">
              <div className="toppings-list-item">
                <div className="left-section">
                  <input
                    type="checkbox"
                    id={`user-checkbox-0`}
                    value="0"
                    defaultChecked={true}
                    onChange={e => {
                      onUserCheckboxChange(e);
                    }}
                  />
                  <label htmlFor={`user-checkbox-0`}>All</label>
                </div>
              </div>
            </li>
            {users.map(({ id, login }, index) => {
              return (
                <li key={index}>
                  <div className="toppings-list-item">
                    <div className="left-section">
                      <input
                        type="checkbox"
                        id={`user-checkbox-${index}`}
                        name={login}
                        value={id}
                        onChange={e => {
                          onUserCheckboxChange(e);
                        }}
                      />
                      <label htmlFor={`user-checkbox-${index}`}>{login}</label>
                    </div>
                  </div>
                </li>
              );
            })}
          </ul>
        </div>
      </div>
      {/* Show by team:<select  id="task-status" name="team" onChange={e => onTeamChange(e.target.value)}>
                {teams.map(team => (
                  <option value={team.id} key={team.id}>
                    {team.name}
                  </option>
                ))}
              </select >
              <br/>
              Show by use:<select  id="task-status" name="user" onChange={e => onTeamChange(e.target.value)}>
                {users.map(team => (
                  <option value={team.id} key={team.id}>
                    {team.login}
                  </option>
                ))}
              </select> */}
      {/* <div style={{ display: 'flex', justifyContent: 'center',width:'100%', height: '100%' , backgroundColor :'#eee'}}> */}

      {/* </div>  */}
      <div className="d-flex flex-row-reverse bd-highlight">
        <DragDropContext onDragEnd={result => onDragEnd(result, columns, setColumns)}>
          {columns
            ? Object.entries(columns).map(([columnId, column], index) => {
                return (
                  <div
                    style={{
                      // display: 'flex',
                      // flexDirection: 'column',
                      // alignItems: 'center',
                      // border:'1px solid',
                      // backgroundColor:'lightgrey'
                      margin: '4px',
                      // padding:'0px'
                    }}
                    key={columnId}
                  >
                    <Droppable droppableId={columnId} key={columnId}>
                      {(provided, snapshot) => {
                        return (
                          <div>
                            <div
                              className="d-flex bd-highlight mb-3"
                              style={{
                                background: '#e3e3e3',
                                padding: 0,
                                margin: 0,
                                width: 300,
                                top: -10,
                                fontWeight: 'bold',
                                minHeight: 10,
                              }}
                            >
                              <div className="me-auto p-2 ">{column.name}</div>
                            </div>
                            <div
                              {...provided.droppableProps}
                              ref={provided.innerRef}
                              style={{
                                background: snapshot.isDraggingOver ? 'lightblue' : '#e3e3e3',
                                margin: 0,
                                padding: 4,
                                width: 300,
                                //   border:'1px solid #adb5bd',
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
                                            //   padding: 16,
                                            //   margin: '0 0 8px 0',
                                            minHeight: '50px',
                                            //   backgroundColor: snapshot.isDragging ? '#263B4A' : '#456C86',
                                            color: 'white',
                                            ...provided.draggableProps.style,
                                          }}
                                        >
                                          <MyCard
                                            title={item.name}
                                            priority={item.priority}
                                            assignedTo={item.assignedTo}
                                            color="#fff"
                                            dueDate={item.dueDate}
                                            team={item.team}
                                          />
                                        </div>
                                      );
                                    }}
                                  </Draggable>
                                );
                              })}
                              {provided.placeholder}
                            </div>
                          </div>
                        );
                      }}
                    </Droppable>
                  </div>
                );
              })
            : []}
        </DragDropContext>
      </div>
    </div>
  );
};
export default TaskKanban;
// When list deleted, delete all cards with that listId, otherwise loads of cards hang around in localstorage
// Remove anys
//https://codesandbox.io/p/sandbox/jovial-leakey-i0ex5?file=%2Fsrc%2FApp.js%3A88%2C17-139%2C29
//https://dev.to/bnevilleoneill/build-a-beautiful-draggable-kanban-board-with-react-beautiful-dnd-3oij
