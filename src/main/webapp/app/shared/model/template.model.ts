import dayjs from 'dayjs';
import { ITemplateTask } from 'app/shared/model/template-task.model';

export interface ITemplate {
  id?: number;
  name?: string;
  description?: string;
  createdDate?: dayjs.Dayjs | null;
  createdBy?: string | null;
  modifiedDate?: dayjs.Dayjs | null;
  modifiedBy?: string | null;
  tasks?: ITemplateTask[] | null;
}

export const defaultValue: Readonly<ITemplate> = {};
