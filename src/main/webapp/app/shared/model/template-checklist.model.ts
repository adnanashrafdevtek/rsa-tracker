import dayjs from 'dayjs';
import { ITemplateTask } from 'app/shared/model/template-task.model';

export interface ITemplateChecklist {
  id?: number;
  name?: string;
  createdDate?: dayjs.Dayjs | null;
  createdBy?: string | null;
  modifiedDate?: dayjs.Dayjs | null;
  modifiedBy?: string | null;
  task?: ITemplateTask | null;
}

export const defaultValue: Readonly<ITemplateChecklist> = {};
